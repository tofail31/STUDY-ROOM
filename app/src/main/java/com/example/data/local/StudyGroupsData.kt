package com.example.data.local

import com.example.data.model.GroupMember
import com.example.data.model.GroupOrganizer
import com.example.data.model.OrganizerRole
import com.example.data.model.StudyGroup
import com.example.data.model.StudyStatus

object StudyGroupsData {

    private val memberNames = listOf(
        "Tahmid H.", "Nusrat J.", "Rayhan K.", "Fatima R.", "Sadiq M.",
        "Maliha T.", "Farhan A.", "Tasnim N.", "Zayed I.", "Sumaiya S.",
        "Arafat B.", "Mim Akter", "Tariq Z.", "Anika F.", "Imran C.",
        "Nabila K.", "Siam R.", "Lamia H.", "Hamza S.", "Raisa M.",
        "Mahmud U.", "Sadia P.", "Mehedi T.", "Tania B.", "Ashraf N.",
        "Rifah Z.", "Nafis Q.", "Jannat L.", "Shafiq D."
    )

    private fun buildMembers(
        groupId: String,
        count: Int,
        studyingCount: Int,
        restingCount: Int,
        subjects: List<Pair<String, String>>,
        seedOffset: Int
    ): List<GroupMember> {
        val safeCount = count.coerceIn(1, 29)
        return (0 until safeCount).map { idx ->
            val name = memberNames[(idx + seedOffset) % memberNames.size]
            val parts = name.split(" ")
            val initials = buildString {
                parts.firstOrNull()?.firstOrNull()?.let { append(it.uppercaseChar()) }
                parts.getOrNull(1)?.firstOrNull()?.let { append(it.uppercaseChar()) }
            }.ifBlank { "ST" }

            val status = when {
                idx < studyingCount -> StudyStatus.STUDYING
                idx < studyingCount + restingCount -> StudyStatus.RESTING
                else -> StudyStatus.OFFLINE
            }

            val baseSec = when (status) {
                StudyStatus.STUDYING -> (18_500L - idx * 720L).coerceAtLeast(1_450L)
                StudyStatus.RESTING -> (9_800L - (idx - studyingCount) * 650L).coerceAtLeast(900L)
                StudyStatus.OFFLINE -> if (idx % 2 == 0) 2_100L else 0L
            }

            val subj = subjects[(idx + seedOffset) % subjects.size]
            GroupMember(
                id = "${groupId}_m_$idx",
                name = name,
                initials = initials,
                subjectEn = subj.first,
                subjectBn = subj.second,
                todayStudySeconds = baseSec,
                status = status,
                streakDays = ((idx * 3 + seedOffset) % 28) + 1
            )
        }
    }

    val groups: List<StudyGroup> = listOf(
        StudyGroup(
            id = "grp_dhaka_quran",
            nameEn = "Dhaka Hifz & Tafsir Circle",
            nameBn = "ঢাকা হিফজ ও তাফসির সার্কেল",
            category = "Qur'an & Hifz",
            city = "Dhaka",
            descriptionEn = "Daily Fajr-to-Isha Qur'an memorization, Muraja'ah, and Tafsir study room for brothers and sisters in Dhaka. Pause cleanly for every Adzan.",
            descriptionBn = "ঢাকার শিক্ষার্থী ও হাফেজদের জন্য দৈনিক কুরআন হিফজ, মুরাজাআহ ও তাফসির স্টাডি গ্রুপ। প্রতিটি আযানের সময় নামাজের বিরতি রাখা হয়।",
            rulesEn = listOf(
                "Keep phone on Focus Timer during Hifz & Tafsir sessions.",
                "Minimum 2 hours of logged study per day.",
                "Automatic Salah pause recommended during Fajr, Dhuhr, Asr, Maghrib & Isha.",
                "Maximum 30 members per group — inactive members for 3 days give seat to newcomers."
            ),
            rulesBn = listOf(
                "হিফজ ও তাফসির চলাকালীন ফোকাস টাইমার চালু রাখুন।",
                "প্রতিদিন ন্যূনতম ২ ঘণ্টা পড়াশোনা সম্পন্ন করুন।",
                "পাঁচ ওয়াক্ত নামাজের সময় বিরতি নিন।",
                "প্রতি গ্রুপে সর্বোচ্চ ৩০ জন সদস্য — ৩ দিন অনুপস্থিত থাকলে আসন খালি করা হয়।"
            ),
            dailyTargetHours = 4,
            rating = 4.9,
            maxMembers = 30,
            badgeTextEn = "Hifz Active",
            badgeTextBn = "হিফজ সক্রিয়",
            organizers = listOf(
                GroupOrganizer("org_q1", "Hafez Abdullah Al-Mahmud", "HA", OrganizerRole.CREATOR, "Dhaka • Founded 2024", "ঢাকা • প্রতিষ্ঠাতা (২০২৪)"),
                GroupOrganizer("org_q2", "Tahmidul Hasan", "TH", OrganizerRole.LEADER, "Fajr & Muraja'ah Lead", "ফজর ও মুরাজাআহ লিডার"),
                GroupOrganizer("org_q3", "Nusrat Jahan", "NJ", OrganizerRole.MODERATOR, "Tajweed & Rules Monitor", "তাজবিদ ও নিয়ম মডারেটর")
            ),
            members = buildMembers(
                groupId = "grp_dhaka_quran",
                count = 24,
                studyingCount = 14,
                restingCount = 6,
                subjects = listOf(
                    "Surah Al-Baqarah" to "সূরা বাকারা",
                    "Juz Amma Hifz" to "জুজ আম্মা হিফজ",
                    "Tafsir Ibn Kathir" to "তাফসির ইবনে কাসির",
                    "Tajweed Practice" to "তাজবিদ অনুশীলন"
                ),
                seedOffset = 0
            )
        ),
        StudyGroup(
            id = "grp_bcs_deen",
            nameEn = "Dhaka BCS & Deen Scholars",
            nameBn = "ঢাকা বিসিএস ও দ্বীন স্কলার্স",
            category = "BCS & Varsity",
            city = "Dhaka",
            descriptionEn = "Disciplined YPT-style focus group for 47th/48th BCS & Bank job aspirants balancing competitive prep with daily Salah and Adhkar.",
            descriptionBn = "বিসিএস ও ব্যাংক জব পরীক্ষার্থীদের জন্য সুশৃঙ্খল ওয়াইপিটি-স্টাইল ফোকাস রুম, যেখানে নামাজ ও যিকিরের সাথে দৈনিক প্রস্তুতি নেওয়া হয়।",
            rulesEn = listOf(
                "Minimum daily target: 5 hours focused study.",
                "Log your subject tag (Bangla, English, GK, Math) before starting.",
                "Take short 10-minute breaks between 50-minute deep study blocks.",
                "30 seats cap — keep your daily streak alive to retain your seat."
            ),
            rulesBn = listOf(
                "দৈনিক ন্যূনতম লক্ষ্য: ৫ ঘণ্টা মনোযোগী পড়াশোনা।",
                "শুরু করার আগে আপনার বিষয় নির্বাচন করুন।",
                "প্রতি ৫০ মিনিট পড়ার পর ১০ মিনিট বিশ্রাম নিন।",
                "৩০ আসনের সীমা — আসন ধরে রাখতে নিয়মিত অধ্যয়ন করুন।"
            ),
            dailyTargetHours = 6,
            rating = 4.8,
            maxMembers = 30,
            badgeTextEn = "High Focus",
            badgeTextBn = "উচ্চ ফোকাস",
            organizers = listOf(
                GroupOrganizer("org_b1", "Shafiqur Rahman", "SR", OrganizerRole.CREATOR, "43rd BCS Cadre • Founder", "৪৩তম বিসিএস • প্রতিষ্ঠাতা"),
                GroupOrganizer("org_b2", "Rayhan Kabir", "RK", OrganizerRole.LEADER, "Daily Focus Coordinator", "দৈনিক ফোকাস লিডার"),
                GroupOrganizer("org_b3", "Maliha Tabassum", "MT", OrganizerRole.MODERATOR, "Attendance & Seat Care", "আসন ও উপস্থিতি মডারেটর")
            ),
            members = buildMembers(
                groupId = "grp_bcs_deen",
                count = 27,
                studyingCount = 17,
                restingCount = 5,
                subjects = listOf(
                    "Bangladesh Affairs" to "বাংলাদেশ বিষয়াবলি",
                    "BCS Math & Mental" to "বিসিএস গণিত",
                    "English Literature" to "ইংরেজি সাহিত্য",
                    "Bangla Grammar" to "বাংলা ব্যাকরণ"
                ),
                seedOffset = 4
            )
        ),
        StudyGroup(
            id = "grp_buet_du",
            nameEn = "BUET & DU Admission Hall",
            nameBn = "বুয়েট ও ঢাবি ভর্তি হল",
            category = "BCS & Varsity",
            city = "Dhaka",
            descriptionEn = "Intensive STEM, Physics, Calculus, and University Admission preparation room inspired by Dhaka University & BUET hall study culture.",
            descriptionBn = "বুয়েট, ঢাকা বিশ্ববিদ্যালয় ও ইঞ্জিনিয়ারিং ভর্তি পরীক্ষার্থীদের জন্য পদার্থবিজ্ঞান, ক্যালকুলাস ও রসায়ন ফোকাস স্টাডি রুম।",
            rulesEn = listOf(
                "Strict 6h+ daily target for engineering & varsity aspirants.",
                "No distraction apps during active focus timer.",
                "Send encouraging Salam cheers to motivate peers.",
                "29/30 seats taken — only 1 open seat remaining!"
            ),
            rulesBn = listOf(
                "ভর্তি পরীক্ষার্থীদের জন্য দৈনিক ৬+ ঘণ্টা ফোকাস লক্ষ্য।",
                "টাইমার চলাকালীন অপ্রয়োজনীয় অ্যাপ ব্যবহার নিষিদ্ধ।",
                "সহপাঠীদের উৎসাহ দিতে সালাম চিয়ার পাঠান।",
                "৩০টি আসনের মধ্যে ২৯টি পূর্ণ — মাত্র ১টি আসন খালি!"
            ),
            dailyTargetHours = 8,
            rating = 4.9,
            maxMembers = 30,
            badgeTextEn = "Almost Full",
            badgeTextBn = "প্রায় পূর্ণ",
            organizers = listOf(
                GroupOrganizer("org_u1", "Engr. Farhan Ahmed", "FA", OrganizerRole.CREATOR, "BUET CSE • Circle Host", "বুয়েট সিএসই • প্রতিষ্ঠাতা"),
                GroupOrganizer("org_u2", "Zayed Islam", "ZI", OrganizerRole.LEADER, "STEM Mock Lead", "স্টেম মক লিডার"),
                GroupOrganizer("org_u3", "Tasnim Nawar", "TN", OrganizerRole.MODERATOR, "Night Session Mod", "নাইট সেশন মডারেটর")
            ),
            members = buildMembers(
                groupId = "grp_buet_du",
                count = 29,
                studyingCount = 21,
                restingCount = 5,
                subjects = listOf(
                    "Higher Math" to "উচ্চতর গণিত",
                    "Physics Mechanics" to "পদার্থবিজ্ঞান",
                    "Organic Chem" to "জৈব রসায়ন",
                    "Mock Question Solve" to "প্রশ্ন সমাধান"
                ),
                seedOffset = 9
            )
        ),
        StudyGroup(
            id = "grp_hsc_science",
            nameEn = "HSC Science & Fajr Focus",
            nameBn = "এইচএসসি বিজ্ঞান ও ফজর ফোকাস",
            category = "SSC / HSC",
            city = "Chattogram & Dhaka",
            descriptionEn = "Early-morning post-Fajr study circle for SSC & HSC science students across Bangladesh. Build barakah into your morning study routine.",
            descriptionBn = "সারা বাংলাদেশের এসএসসি ও এইচএসসি বিজ্ঞান শিক্ষার্থীদের জন্য ফজরের পর বরকতময় সকালের স্টাডি সার্কেল।",
            rulesEn = listOf(
                "Start at least one morning session right after Fajr.",
                "Minimum daily study goal: 3 hours.",
                "Switch status to Resting when taking meal or prayer breaks.",
                "Max 30 members per study room."
            ),
            rulesBn = listOf(
                "ফজরের পর অন্তত একটি সকালের সেশন শুরু করুন।",
                "দৈনিক ন্যূনতম লক্ষ্য: ৩ ঘণ্টা।",
                "নামাজ বা খাবারের বিরতিতে রেস্টিং স্ট্যাটাস দিন।",
                "প্রতি স্টাডি রুমে সর্বোচ্চ ৩০ জন সদস্য।"
            ),
            dailyTargetHours = 5,
            rating = 4.7,
            maxMembers = 30,
            badgeTextEn = "Fajr Streak",
            badgeTextBn = "ফজর স্ট্রিক",
            organizers = listOf(
                GroupOrganizer("org_h1", "Sadiqul Islam", "SI", OrganizerRole.CREATOR, "DU Physics • Founder", "ঢাবি পদার্থবিজ্ঞান • প্রতিষ্ঠাতা"),
                GroupOrganizer("org_h2", "Sumaiya Sultana", "SS", OrganizerRole.LEADER, "Post-Fajr Focus Lead", "ফজর পরবর্তী ফোকাস লিডার"),
                GroupOrganizer("org_h3", "Siam Chowdhury", "SC", OrganizerRole.MODERATOR, "Science Doubts Mod", "বিজ্ঞান ও নিয়ম মডারেটর")
            ),
            members = buildMembers(
                groupId = "grp_hsc_science",
                count = 22,
                studyingCount = 12,
                restingCount = 6,
                subjects = listOf(
                    "HSC Biology" to "এইচএসসি জীববিজ্ঞান",
                    "Physics 2nd Paper" to "পদার্থ ২য় পত্র",
                    "ICT & Logic" to "আইসিটি ও লজিক",
                    "Chemistry" to "রসায়ন"
                ),
                seedOffset = 13
            )
        ),
        StudyGroup(
            id = "grp_medical_bd",
            nameEn = "DMC Medical Prep Circle",
            nameBn = "ডিএমসি মেডিকেল প্রস্তুতি",
            category = "BCS & Varsity",
            city = "Dhaka",
            descriptionEn = "Focused Biology, Chemistry, and GK revision room for MBBS & Medical admission students aiming for Dhaka Medical College & top public medicals.",
            descriptionBn = "ঢাকা মেডিকেল ও সরকারি মেডিকেল ভর্তি পরীক্ষার্থীদের জন্য জীববিজ্ঞান, রসায়ন ও সাধারণ জ্ঞান রিভিশন রুম।",
            rulesEn = listOf(
                "Target 7 hours daily with active Pomodoro or stopwatch focus.",
                "Log Biology / Chemistry / GK clearly.",
                "Respect prayer & rest breaks to prevent burnout.",
                "Cap of 30 dedicated students per group."
            ),
            rulesBn = listOf(
                "দৈনিক ৭ ঘণ্টা মনোযোগী রিভিশন লক্ষ্য।",
                "জীববিজ্ঞান, রসায়ন বা জিকে বিষয় নির্বাচন করুন।",
                "ক্লান্তি এড়াতে নামাজ ও বিশ্রামের বিরতি নিন।",
                "প্রতি গ্রুপে সর্বোচ্চ ৩০ জন নিবেদিত শিক্ষার্থী।"
            ),
            dailyTargetHours = 7,
            rating = 4.8,
            maxMembers = 30,
            badgeTextEn = "MBBS Focus",
            badgeTextBn = "মেডিকেল ফোকাস",
            organizers = listOf(
                GroupOrganizer("org_m1", "Dr. Mahmudul Hasan", "MH", OrganizerRole.CREATOR, "DMC K-75 • Creator", "ডিএমসি • প্রতিষ্ঠাতা"),
                GroupOrganizer("org_m2", "Fatima Rahman", "FR", OrganizerRole.LEADER, "Biology & GK Lead", "জীববিজ্ঞান ও জিকে লিডার"),
                GroupOrganizer("org_m3", "Anika Farhana", "AF", OrganizerRole.MODERATOR, "Daily Check-in Mod", "দৈনিক উপস্থিতি মডারেটর")
            ),
            members = buildMembers(
                groupId = "grp_medical_bd",
                count = 25,
                studyingCount = 16,
                restingCount = 5,
                subjects = listOf(
                    "Zoology & Botany" to "প্রাণি ও উদ্ভিদবিজ্ঞান",
                    "Medical GK" to "মেডিকেল সাধারণ জ্ঞান",
                    "Chemistry Quantitative" to "পরিমাণগত রসায়ন",
                    "English Med Prep" to "মেডিকেল ইংরেজি"
                ),
                seedOffset = 18
            )
        ),
        StudyGroup(
            id = "grp_kotlin_devs",
            nameEn = "Bangladesh Tech & Kotlin Devs",
            nameBn = "বাংলাদেশ টেক ও কোটলিন ডেভস",
            category = "Tech & Coding",
            city = "Dhaka",
            descriptionEn = "Deep-work coding, Jetpack Compose, DSA, and CS study room for Bangladeshi university students and software engineers.",
            descriptionBn = "বাংলাদেশি সিএসই শিক্ষার্থী ও সফটওয়্যার ইঞ্জিনিয়ারদের জন্য ডিপ-ওয়ার্ক কোডিং, অ্যালগরিদম ও ডেভেলপমেন্ট ফোকাস রুম।",
            rulesEn = listOf(
                "Commit to distraction-free coding sprints (45-90 mins).",
                "Minimum 2 hours deep work logged daily.",
                "Keep Salah breaks mindful and punctual.",
                "Max 30 members per room."
            ),
            rulesBn = listOf(
                "মনোযোগী কোডিং স্প্রিন্ট (৪৫-৯০ মিনিট) সম্পন্ন করুন।",
                "দৈনিক ন্যূনতম ২ ঘণ্টা ডিপ ওয়ার্ক লগ করুন।",
                "সময়মতো নামাজের বিরতি নিন।",
                "প্রতি রুমে সর্বোচ্চ ৩০ জন সদস্য।"
            ),
            dailyTargetHours = 4,
            rating = 4.8,
            maxMembers = 30,
            badgeTextEn = "Deep Work",
            badgeTextBn = "ডিপ ওয়ার্ক",
            organizers = listOf(
                GroupOrganizer("org_d1", "Tariqul Zaman", "TZ", OrganizerRole.CREATOR, "Android Architect • Host", "অ্যান্ড্রয়েড ইঞ্জিনিয়ার • প্রতিষ্ঠাতা"),
                GroupOrganizer("org_d2", "Imran Chowdhury", "IC", OrganizerRole.LEADER, "DSA Sprint Lead", "ডিএসএ স্প্রিন্ট লিডার"),
                GroupOrganizer("org_d3", "Lamia Hoque", "LH", OrganizerRole.MODERATOR, "Deep Work Moderator", "ডিপ ওয়ার্ক মডারেটর")
            ),
            members = buildMembers(
                groupId = "grp_kotlin_devs",
                count = 19,
                studyingCount = 11,
                restingCount = 4,
                subjects = listOf(
                    "Kotlin & Compose" to "কোটলিন ও কম্পোজ",
                    "DSA & LeetCode" to "অ্যালগরিদম অনুশীলন",
                    "System Design" to "সিস্টেম ডিজাইন",
                    "Backend API" to "ব্যাকএন্ড এপিআই"
                ),
                seedOffset = 22
            )
        ),
        StudyGroup(
            id = "grp_arabic_nahw",
            nameEn = "Arabic Grammar & Nahw Study",
            nameBn = "আরবি ব্যাকরণ ও নাহু চর্চা",
            category = "Qur'an & Hifz",
            city = "Sylhet & Dhaka",
            descriptionEn = "Study classical Arabic, Madinah Books, Nahw, and Sarf together so you understand the Qur'an directly in Arabic.",
            descriptionBn = "সরাসরি আরবিতে কুরআন বোঝার জন্য মাদিনা আরবি, নাহু ও সরফ একসাথে চর্চা করার স্টাডি গ্রুপ।",
            rulesEn = listOf(
                "Practice Arabic vocabulary & Nahw daily.",
                "Minimum 90 minutes focus per day.",
                "Respectful and encouraging Islamic brotherhood/sisterhood.",
                "Limited to 30 members per circle."
            ),
            rulesBn = listOf(
                "প্রতিদিন আরবি শব্দভান্ডার ও নাহু অনুশীলন করুন।",
                "দৈনিক ন্যূনতম ৯০ মিনিট অধ্যয়ন করুন।",
                "সহপাঠীদের প্রতি শ্রদ্ধাশীল ও সহায়ক থাকুন।",
                "প্রতি সার্কেলে সর্বোচ্চ ৩০ জন সদস্য।"
            ),
            dailyTargetHours = 3,
            rating = 4.7,
            maxMembers = 30,
            badgeTextEn = "Arabic Circle",
            badgeTextBn = "আরবি সার্কেল",
            organizers = listOf(
                GroupOrganizer("org_a1", "Ustadh Ashraf Nabil", "AN", OrganizerRole.CREATOR, "Madinah Arabic • Creator", "মাদিনা আরবি • প্রতিষ্ঠাতা"),
                GroupOrganizer("org_a2", "Hamza Siddiqui", "HS", OrganizerRole.LEADER, "Nahw & Sarf Lead", "নাহু ও সরফ লিডার"),
                GroupOrganizer("org_a3", "Raisa Mahbub", "RM", OrganizerRole.MODERATOR, "Vocab & Circle Mod", "শব্দার্থ ও নিয়ম মডারেটর")
            ),
            members = buildMembers(
                groupId = "grp_arabic_nahw",
                count = 21,
                studyingCount = 10,
                restingCount = 7,
                subjects = listOf(
                    "Madinah Book 2" to "মাদিনা বুক ২",
                    "Ajrumiyyah Nahw" to "আজরুমিয়্যাহ নাহু",
                    "Sarf Conjugation" to "সরফ অনুশীলন",
                    "Qur'anic Vocab" to "কুরআনিক শব্দার্থ"
                ),
                seedOffset = 7
            )
        )
    )
}
