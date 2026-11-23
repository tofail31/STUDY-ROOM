package com.example.data.local

import com.example.data.model.CustomPublishedExam
import com.example.data.model.ExamPaperStyle
import com.example.data.model.ExamQuestion

object ExamGroundDefaults {
    val defaultCommunityExams: List<CustomPublishedExam> = listOf(
        CustomPublishedExam(
            id = "exam_comm_phy_vector",
            title = "HSC Vector & Dot Product Rapid Test",
            authorName = "Farhan Sadik",
            authorBatch = "HSC-26 • NDC",
            subjectId = "trk_physics",
            subjectName = "Physics",
            subjectColorHex = 0xFFE66D45,
            paper = 1,
            chapterId = "trk_physics_p1_ch_2",
            chapterTitle = "ভেক্টর",
            password = "",
            description = "Quick conceptual + Uddipok MCQ set covering orthogonal vectors, cross product, and river-boat velocity.",
            rules = "1. Answer all 3 questions carefully.\n2. No external calculator needed.\n3. Check full explanation after submit.",
            showDetailsInEntryPopup = true,
            isPublished = true,
            createdAtMillis = 1763600000000L,
            attemptCount = 48,
            questions = listOf(
                ExamQuestion(
                    id = "comm_phy_q1",
                    subjectId = "trk_physics",
                    chapterId = "trk_physics_p1_ch_2",
                    paper = 1,
                    uddipok = "একজন মাঝি ৪ km/h বেগে প্রবহমান নদীর এক তীর থেকে লম্বভাবে ৮ km/h বেগে নৌকা চালিয়ে অপর তীরে পৌঁছাতে চায়।",
                    questionText = "নৌকাটি সরাসরি অপর তীরে পৌঁছাতে চাইলে স্রোতের বিপরীতে কত কোণে যাত্রা করতে হবে?",
                    options = listOf("90°", "120°", "60°", "150°"),
                    correctIndex = 1,
                    explanation = "লব্ধি স্রোতের সাথে 90° কোণ করলে cos(α) = -u/v = -4/8 = -0.5, অর্থাৎ α = 120°।"
                ),
                ExamQuestion(
                    id = "comm_phy_q2",
                    subjectId = "trk_physics",
                    chapterId = "trk_physics_p1_ch_2",
                    paper = 1,
                    uddipok = "",
                    questionText = "দুটি ভেক্টর A = 2i + 3j + k এবং B = 4i - 2j - 2k পরস্পর কেমন হবে?",
                    options = listOf("সমান্তরাল", "পরস্পর লম্ব (Perpendicular)", "বিপরীতমুখী", "সমরেখ"),
                    correctIndex = 1,
                    explanation = "A·B = (2)(4) + (3)(-2) + (1)(-2) = 8 - 6 - 2 = 0, তাই ভেক্টরদ্বয় পরস্পর লম্ব।"
                ),
                ExamQuestion(
                    id = "comm_phy_q3",
                    subjectId = "trk_physics",
                    chapterId = "trk_physics_p1_ch_2",
                    paper = 1,
                    uddipok = "",
                    questionText = "A × B এর মান শূন্য হলে ভেক্টরদ্বয়ের মধ্যবর্তী কোণ কত?",
                    options = listOf("90°", "0° অথবা 180°", "45°", "60°"),
                    correctIndex = 1,
                    explanation = "|A × B| = AB sinθ = 0 ⇒ sinθ = 0 ⇒ θ = 0° বা 180°।"
                )
            )
        ),
        CustomPublishedExam(
            id = "exam_comm_chem_organic",
            title = "Organic Chemistry Revision (Locked Room)",
            authorName = "Nusrat Jahan",
            authorBatch = "HSC-26 • Viqarunnisa",
            subjectId = "trk_chemistry",
            subjectName = "Chemistry",
            subjectColorHex = 0xFF7B61FF,
            paper = 2,
            chapterId = "trk_chemistry_p2_ch_2",
            chapterTitle = "জৈব রসায়ন",
            password = "2026",
            description = "Circle practice test for Organic Chemistry reactions (Passcode hint: 2026).",
            rules = "1. Enter circle passcode (2026) to unlock.\n2. Single attempt recommended for honest score.",
            showDetailsInEntryPopup = true,
            isPublished = true,
            createdAtMillis = 1763550000000L,
            attemptCount = 31,
            questions = listOf(
                ExamQuestion(
                    id = "comm_chem_q1",
                    subjectId = "trk_chemistry",
                    chapterId = "trk_chemistry_p2_ch_2",
                    paper = 2,
                    uddipok = "CH₃-CH=CH₂ যৌগটির সাথে HBr যুক্ত হয়ে দুটি সম্ভাব্য উৎপাদ তৈরি হয়।",
                    questionText = "মারকনিকভ নীতি অনুযায়ী প্রোপিনের সাথে HBr-এর বিক্রিয়ায় প্রধান উৎপাদ কোনটি?",
                    options = listOf("1-ব্রোমোপ্রোপেন", "2-ব্রোমোপ্রোপেন", "প্রোপানল", "প্রোপিন অক্সাইড"),
                    correctIndex = 1,
                    explanation = "মারকনিকভ নিয়মে হাইড্রোজেন বেশি H-যুক্ত কার্বনে এবং Br কম H-যুক্ত কার্বনে যুক্ত হয়ে 2-ব্রোমোপ্রোপেন দেয়।"
                ),
                ExamQuestion(
                    id = "comm_chem_q2",
                    subjectId = "trk_chemistry",
                    chapterId = "trk_chemistry_p2_ch_2",
                    paper = 2,
                    uddipok = "",
                    questionText = "বেনজিন বলয়ে কোন ধরনের বিক্রিয়া প্রধানত ঘটে?",
                    options = listOf("কেন্দ্রাকর্ষী সংযোজন", "ইলেকট্রোফিলিক প্রতিস্থাপন", "মুক্তমূলক সংযোজন", "অপসারণ"),
                    correctIndex = 1,
                    explanation = "অ্যারোমেটিক যৌগ হওয়ায় বেনজিন π-ইলেকট্রনের সমৃদ্ধ বলয়ে ইলেকট্রোফিলিক প্রতিস্থাপন বিক্রিয়া দেয়।"
                )
            )
        ),
        CustomPublishedExam(
            id = "exam_comm_bio_cell",
            title = "Cell Structure & Division Quick Check",
            authorName = "Tahmid Hasan",
            authorBatch = "HSC-26 • Dhaka College",
            subjectId = "trk_biology",
            subjectName = "Biology",
            subjectColorHex = 0xFF3E8E63,
            paper = 1,
            chapterId = "trk_biology_p1_ch_1",
            chapterTitle = "কোষ ও এর গঠন",
            password = "",
            description = "Botany Chapter 1 high-yield board & medical foundation MCQs.",
            rules = "1. Read both Uddipok and options closely.\n2. Ideal time: 2 minutes.",
            showDetailsInEntryPopup = true,
            paperStyle = ExamPaperStyle.PLAIN_FLOOR,
            isPublished = true,
            createdAtMillis = 1763500000000L,
            attemptCount = 62,
            questions = listOf(
                ExamQuestion(
                    id = "comm_bio_q1",
                    subjectId = "trk_biology",
                    chapterId = "trk_biology_p1_ch_1",
                    paper = 1,
                    uddipok = "একটি দ্বি-স্তরী আবরণবেষ্টিত কোষীয় অঙ্গাণুর ভেতরের স্তর ভাঁজ হয়ে কৃস্টি গঠন করে এবং ATP উৎপন্ন করে।",
                    questionText = "উদ্দীপকের অঙ্গাণুটি কোনটি?",
                    options = listOf("গলগি বডি", "মাইটোকন্ড্রিয়া", "লাইসোজোম", "রাইবোজোম"),
                    correctIndex = 1,
                    explanation = "মাইটোকন্ড্রিয়ার অন্তঃআবরণীর ভাঁজকে কৃস্টি বলে এবং এটি কোষের পাওয়ার হাউস হিসেবে ATP তৈরি করে।"
                ),
                ExamQuestion(
                    id = "comm_bio_q2",
                    subjectId = "trk_biology",
                    chapterId = "trk_biology_p1_ch_1",
                    paper = 1,
                    uddipok = "",
                    questionText = "কোষের 'আত্মঘাতী থলিকা' (Suicidal Bag) বলা হয় কাকে?",
                    options = listOf("রাইবোজোম", "লাইসোজোম", "পেরক্সিজোম", "সেন্ট্রোসোম"),
                    correctIndex = 1,
                    explanation = "লাইসোজোমে থাকা হাইড্রোলাইটিক এনজাইম প্রয়োজনে নিজ কোষকে পরিপাক (Autolysis) করতে পারে।"
                )
            )
        )
    )
}
