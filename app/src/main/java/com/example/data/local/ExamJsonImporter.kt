package com.example.data.local

import com.example.data.model.ExamPaperStyle
import com.example.data.model.ExamQuestion
import org.json.JSONArray
import org.json.JSONObject

data class ParsedExamJsonResult(
    val subjectName: String? = null,
    val examHeading: String? = null,
    val password: String? = null,
    val description: String? = null,
    val rules: String? = null,
    val paperStyle: ExamPaperStyle? = null,
    val questions: List<ExamQuestion> = emptyList()
)

object ExamJsonImporter {

    fun parse(rawInput: String): Result<ParsedExamJsonResult> {
        val cleaned = stripCodeFences(rawInput.trim())
        if (cleaned.isEmpty()) {
            return Result.failure(IllegalArgumentException("Please paste JSON code first."))
        }

        return try {
            val timestamp = System.currentTimeMillis()
            if (cleaned.startsWith("[")) {
                val arr = JSONArray(cleaned)
                val questions = parseQuestionsArray(arr, timestamp)
                if (questions.isEmpty()) {
                    Result.failure(IllegalArgumentException("No valid questions found in JSON array."))
                } else {
                    Result.success(ParsedExamJsonResult(questions = questions))
                }
            } else if (cleaned.startsWith("{")) {
                val root = JSONObject(cleaned)
                val subjectName = firstNonBlank(root, "subjectName", "subject", "subj")
                val examHeading = firstNonBlank(root, "title", "heading", "examHeading", "name")
                val password = firstNonBlank(root, "password", "passcode", "pin")
                val description = firstNonBlank(root, "description", "desc")
                val rules = firstNonBlank(root, "rules", "rule")
                val rawStyle = firstNonBlank(root, "paperStyle", "style", "layout")
                val paperStyle = when {
                    rawStyle == null -> null
                    rawStyle.contains("plain", ignoreCase = true) ||
                        rawStyle.contains("text", ignoreCase = true) ||
                        rawStyle == "2" -> ExamPaperStyle.PLAIN_FLOOR
                    rawStyle.contains("card", ignoreCase = true) ||
                        rawStyle.contains("box", ignoreCase = true) ||
                        rawStyle == "1" -> ExamPaperStyle.CLASSIC_CARDS
                    else -> null
                }

                val qArr = root.optJSONArray("questions")
                    ?: root.optJSONArray("items")
                    ?: root.optJSONArray("mcqs")
                    ?: root.optJSONArray("data")

                val questions = if (qArr != null) {
                    parseQuestionsArray(qArr, timestamp)
                } else {
                    // Single question JSON object fallback
                    val single = parseQuestionObject(root, 0, timestamp)
                    if (single != null) listOf(single) else emptyList()
                }

                if (questions.isEmpty()) {
                    Result.failure(
                        IllegalArgumentException("No valid questions found. Each question needs question text and 4 options.")
                    )
                } else {
                    Result.success(
                        ParsedExamJsonResult(
                            subjectName = subjectName,
                            examHeading = examHeading,
                            password = password,
                            description = description,
                            rules = rules,
                            paperStyle = paperStyle,
                            questions = questions
                        )
                    )
                }
            } else {
                Result.failure(IllegalArgumentException("JSON must start with '{' or '['."))
            }
        } catch (e: Exception) {
            Result.failure(IllegalArgumentException("Invalid JSON format: ${e.message ?: "check syntax"}"))
        }
    }

    private fun parseQuestionsArray(arr: JSONArray, timestamp: Long): List<ExamQuestion> {
        val result = mutableListOf<ExamQuestion>()
        for (i in 0 until arr.length()) {
            val obj = arr.optJSONObject(i) ?: continue
            val parsed = parseQuestionObject(obj, i, timestamp) ?: continue
            result.add(parsed)
        }
        return result
    }

    private fun parseQuestionObject(obj: JSONObject, index: Int, timestamp: Long): ExamQuestion? {
        val qText = firstNonBlank(obj, "questionText", "question", "q", "prompt", "text") ?: return null
        val uddipok = firstNonBlank(obj, "uddipok", "stimulus", "passage", "stem").orEmpty()
        val explanation = firstNonBlank(obj, "explanation", "explain", "solution").orEmpty()

        val options = extractOptions(obj)
        if (options.size < 2) return null
        val paddedOptions = if (options.size >= 4) {
            options.take(4)
        } else {
            options + List(4 - options.size) { "-" }
        }

        val correctIdx = extractCorrectIndex(obj, paddedOptions)
        val qId = firstNonBlank(obj, "id") ?: "jq_${timestamp}_${index + 1}"

        return ExamQuestion(
            id = qId,
            subjectId = "custom_subj",
            chapterId = "",
            paper = 1,
            questionText = qText,
            options = paddedOptions,
            correctIndex = correctIdx,
            explanation = explanation,
            uddipok = uddipok
        )
    }

    private fun extractOptions(obj: JSONObject): List<String> {
        val optsArr = obj.optJSONArray("options") ?: obj.optJSONArray("choices")
        if (optsArr != null) {
            val out = mutableListOf<String>()
            for (k in 0 until optsArr.length()) {
                val value = optsArr.optString(k).trim()
                if (value.isNotEmpty()) out.add(value)
            }
            if (out.isNotEmpty()) return out
        }

        val candidateGroups = listOf(
            listOf("a", "b", "c", "d"),
            listOf("A", "B", "C", "D"),
            listOf("opt1", "opt2", "opt3", "opt4"),
            listOf("optionA", "optionB", "optionC", "optionD"),
            listOf("ক", "খ", "গ", "ঘ")
        )
        for (keys in candidateGroups) {
            val vals = keys.mapNotNull { key ->
                obj.optString(key, "").trim().takeIf { it.isNotEmpty() }
            }
            if (vals.size >= 2) return vals
        }
        return emptyList()
    }

    private fun extractCorrectIndex(obj: JSONObject, options: List<String>): Int {
        val numericKeys = listOf("correctIndex", "answerIndex", "correct", "answer", "ans")
        for (key in numericKeys) {
            if (!obj.has(key)) continue
            val raw = obj.opt(key) ?: continue
            when (raw) {
                is Number -> return raw.toInt().coerceIn(0, options.lastIndex)
                is String -> {
                    val trimmed = raw.trim()
                    trimmed.toIntOrNull()?.let { return it.coerceIn(0, options.lastIndex) }
                    when (trimmed.lowercase()) {
                        "a", "ক" -> return 0
                        "b", "খ" -> return 1
                        "c", "গ" -> return 2
                        "d", "ঘ" -> return 3
                    }
                    val exactMatch = options.indexOfFirst { it.equals(trimmed, ignoreCase = true) }
                    if (exactMatch >= 0) return exactMatch
                }
            }
        }
        return 0
    }

    private fun firstNonBlank(obj: JSONObject, vararg keys: String): String? {
        for (key in keys) {
            if (obj.has(key) && !obj.isNull(key)) {
                val value = obj.optString(key, "").trim()
                if (value.isNotEmpty()) return value
            }
        }
        return null
    }

    private fun stripCodeFences(raw: String): String {
        var text = raw.trim()
        if (text.startsWith("```")) {
            val firstNewline = text.indexOf('\n')
            if (firstNewline >= 0) {
                text = text.substring(firstNewline + 1)
            }
            if (text.endsWith("```")) {
                text = text.removeSuffix("```")
            }
        }
        return text.trim()
    }

    fun sampleJsonTemplate(isBangla: Boolean): String {
        return if (isBangla) {
            """
{
  "subject": "পদার্থবিজ্ঞান ১ম পত্র",
  "title": "কুইক মডেল টেস্ট ০১",
  "paperStyle": "PLAIN_FLOOR",
  "questions": [
    {
      "uddipok": "একটি বস্তু 20 m/s বেগে খাড়া উপরের দিকে নিক্ষেপ করা হলো (g = 10 m/s²)।",
      "question": "বস্তুটি সর্বাধিক কত উচ্চতায় উঠবে?",
      "options": ["10 m", "20 m", "40 m", "15 m"],
      "correctIndex": 1,
      "explanation": "H = u² / 2g = 400 / 20 = 20 m।"
    },
    {
      "question": "বলের SI একক কোনটি?",
      "options": ["জুল", "নিউটন", "ওয়াট", "প্যাসকেল"],
      "correctIndex": 1,
      "explanation": "বলের SI একক হলো নিউটন (N)।"
    }
  ]
}
            """.trimIndent()
        } else {
            """
{
  "subject": "Physics 1st Paper",
  "title": "Quick JSON Model Test 01",
  "paperStyle": "PLAIN_FLOOR",
  "questions": [
    {
      "uddipok": "A ball is thrown vertically upward with velocity 20 m/s (g = 10 m/s^2).",
      "question": "What is the maximum height reached by the ball?",
      "options": ["10 m", "20 m", "40 m", "15 m"],
      "correctIndex": 1,
      "explanation": "H = u^2 / (2g) = 400 / 20 = 20 m."
    },
    {
      "question": "What is the SI unit of Force?",
      "options": ["Joule", "Newton", "Watt", "Pascal"],
      "correctIndex": 1,
      "explanation": "Force is measured in Newtons (N)."
    }
  ]
}
            """.trimIndent()
        }
    }
}
