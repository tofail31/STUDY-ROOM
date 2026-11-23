package com.example.data.local

import com.example.data.model.ExamQuestion

internal object ExamBankPart1 {

    private fun q(
        chapId: String,
        subjId: String,
        paper: Int,
        num: Int,
        text: String,
        a: String,
        b: String,
        c: String,
        d: String,
        ans: Int,
        exp: String
    ) = ExamQuestion(
        id = "${chapId}_q$num",
        subjectId = subjId,
        chapterId = chapId,
        paper = paper,
        questionText = text,
        options = listOf(a, b, c, d),
        correctIndex = ans,
        explanation = exp
    )

    val questions: Map<String, List<ExamQuestion>> = mapOf(
        // ---------------- BANGLA 1ST PAPER (15 chapters x 3) ----------------
        "trk_bangla_p1_ch_1" to listOf(
            q("trk_bangla_p1_ch_1", "trk_bangla", 1, 1, "'অপরিচিতা' গল্পের কথক অনুপমের বয়স কত ছিল?", "২৩ বছর", "২৭ বছর", "২৫ বছর", "২৯ বছর", 1, "গল্পের শুরুতে অনুপম নিজের বয়স সাতাশ বছর বলে উল্লেখ করেছে।"),
            q("trk_bangla_p1_ch_1", "trk_bangla", 1, 2, "কল্যাণীর পিতার নাম কী?", "হরিশ", "শম্ভুনাথ সেন", "বিনু দাদা", "মামা", 1, "কল্যাণীর পিতা ছিলেন কানপুরের প্রখ্যাত ডাক্তার শম্ভুনাথ সেন।"),
            q("trk_bangla_p1_ch_1", "trk_bangla", 1, 3, "'অপরিচিতা' গল্পে অনুপমের প্রকৃত অভিভাবক কে ছিলেন?", "পিতা", "মামা", "মাতা", "হরিশ", 1, "অনুপমের বাবার মৃত্যুর পর তার মামাই সংসারের প্রধান অভিভাবক হয়ে ওঠেন।")
        ),
        "trk_bangla_p1_ch_2" to listOf(
            q("trk_bangla_p1_ch_2", "trk_bangla", 1, 1, "'বিলাসী' গল্পে মৃত্যুঞ্জয়ের বাগানের আয়তন কত ছিল?", "দশ-বারো বিঘা", "কুড়ি-পঁচিশ বিঘা", "পাঁচ-ছয় বিঘা", "পনেরো বিঘা", 1, "শরৎচন্দ্র চট্টোপাধ্যায়ের 'বিলাসী' গল্পে বাগানটির আয়তন ছিল কুড়ি-পঁচিশ বিঘা।"),
            q("trk_bangla_p1_ch_2", "trk_bangla", 1, 2, "মৃত্যুঞ্জয় কী রোগে আক্রান্ত হয়ে দীর্ঘদিন শয্যাশায়ী ছিল?", "বসন্ত", "ম্যালেরিয়া ও যক্ষ্মা", "কলেরা", "টাইফয়েড", 1, "অবহেলা ও অযত্নে মৃত্যুঞ্জয় ম্যালেরিয়া ও কঠিন পীড়ায় ভুগছিল; বিলাসী তার সেবা করে।"),
            q("trk_bangla_p1_ch_2", "trk_bangla", 1, 3, "মৃত্যুঞ্জয়ের মৃত্যুর পর বিলাসী কীভাবে মৃত্যুবরণ করে?", "সর্পদংশনে", "বিষপানে", "নদীতে ডুবে", "অসুস্থতায়", 1, "মৃত্যুঞ্জয়ের মৃত্যুর সপ্তম দিনে বিলাসী বিষপান করে আত্মহনন করে।")
        ),
        "trk_bangla_p1_ch_3" to listOf(
            q("trk_bangla_p1_ch_3", "trk_bangla", 1, 1, "কাজী নজরুল ইসলামের 'আমার পথ' প্রবন্ধটি কোন গ্রন্থ থেকে সংকলিত?", "যুগবাণী", "রুদ্র-মঙ্গল", "দুর্দিনের যাত্রী", "রাজবন্দীর জবানবন্দী", 1, "'আমার পথ' প্রবন্ধটি কাজী নজরুল ইসলামের 'রুদ্র-মঙ্গল' প্রবন্ধগ্রন্থের অন্তর্গত।"),
            q("trk_bangla_p1_ch_3", "trk_bangla", 1, 2, "'আমার পথ' প্রবন্ধে লেখকের কর্ণধার কে?", "গুরু", "আত্মবিশ্বাস", "সত্য", "সমাজ", 2, "নজরুল স্পষ্ট ঘোষণা করেছেন— 'আমার কর্ণধার আমি; আমার পথ দেখাবে আমার সত্য।'"),
            q("trk_bangla_p1_ch_3", "trk_bangla", 1, 3, "লেখকের মতে কোন জিনিস মানুষকে সবচেয়ে বেশি ছোট করে ফেলে?", "দারিদ্র্য", "অতিরিক্ত বিনয় ও পরাবলম্বন", "রাগ", "অধ্যয়ন", 1, "নিজের সত্যকে অস্বীকার করে ভন্ড বিনয় ও পরাবলম্বন মানুষকে দাসত্বের দিকে নিয়ে যায়।")
        ),
        "trk_bangla_p1_ch_4" to listOf(
            q("trk_bangla_p1_ch_4", "trk_bangla", 1, 1, "'মানব-কল্যাণ' প্রবন্ধের রচয়িতা কে?", "মোতাহের হোসেন চৌধুরী", "আবুল ফজল", "কাজী মোতাহার হোসেন", "আহমদ ছফা", 1, "মানবতাবাদী প্রাবন্ধিক আবুল ফজল 'মানব-কল্যাণ' প্রবন্ধটি রচনা করেন।"),
            q("trk_bangla_p1_ch_4", "trk_bangla", 1, 2, "প্রাবন্ধিকের মতে মনুষ্যত্বের অবমাননা ঘটে কখন?", "ভিক্ষুককে দান করার মাধ্যমে", "এক মুষ্টি ভিক্ষা দিয়ে দাতার অহংবোধে", "কাজে সাহায্য করলে", "শিক্ষা দিলে", 1, "করুণাবশত দান-খয়রাতকে মানব-কল্যাণ মনে করা মানবমর্যাদার অবমাননা।"),
            q("trk_bangla_p1_ch_4", "trk_bangla", 1, 3, "প্রকৃত মানব-কল্যাণের উৎস কী?", "অর্থ-সম্পদ", "মানবমর্যাদা ও সম-অধিকার প্রতিষ্ঠা", "প্রচার", "ভিক্ষা বৃত্তি", 1, "মানুষের মর্যাদা বৃদ্ধি ও স্বাধীন বিকাশের পরিবেশ সৃষ্টিই প্রকৃত মানব-কল্যাণ।")
        ),
        "trk_bangla_p1_ch_5" to listOf(
            q("trk_bangla_p1_ch_5", "trk_bangla", 1, 1, "'মাসি-পিসি' গল্পের পটভূমিতে কোন ঐতিহাসিক দুর্যোগের ছায়া আছে?", "১৯৭১-এর যুদ্ধ", "তাল্লিশের মন্বন্তর (১৯৪৩)", "ভাষা আন্দোলন", "১৯৫৪-র বন্যা", 1, "মানিক বন্দ্যোপাধ্যায়ের 'মাসি-পিসি' ১৯৪৩ সালের দুর্ভিক্ষপীড়িত বাংলার প্রেক্ষাপটে রচিত।"),
            q("trk_bangla_p1_ch_5", "trk_bangla", 1, 2, "মাসি-পিসির প্রধান পেশা কী ছিল?", "কাঁথা সেলাই", "সালতি বেয়ে শাকসবজি বিক্রি", "ধান ভানা", "মাছ ধরা", 1, "আহ্লাদিকে রক্ষা ও জীবিকার তাগিদে মাসি ও পিসি খালের পথে সালতি বেয়ে তরকারি বিক্রি করত।"),
            q("trk_bangla_p1_ch_5", "trk_bangla", 1, 3, "গোকুল কেন আহ্লাদিকে তার বাড়িতে নিয়ে যেতে চেয়েছিল?", "আহ্লাদির প্রতি ভালোবাসায়", "অন্যায় লালসা চরিতার্থ করতে", "পড়ার জন্য", "কাজের জন্য", 1, "স্বার্থপর ও লোভী গোকুল ক্ষমতার দাপটে আহ্লাদিকে নিজের কব্জায় নিতে চেয়েছিল।")
        ),
        "trk_bangla_p1_ch_6" to listOf(
            q("trk_bangla_p1_ch_6", "trk_bangla", 1, 1, "'বায়ান্নর দিনগুলো' রচনাটি কোন গ্রন্থ থেকে গৃহীত?", "কারাগারের রোজনামচা", "অসমাপ্ত আত্মজীবনী", "আমার দেখা নয়াচীন", "সাত সাগরের মাঝি", 1, "জাতির পিতা বঙ্গবন্ধু শেখ মুজিবুর রহমানের 'অসমাপ্ত আত্মজীবনী' থেকে এটি নেওয়া হয়েছে।"),
            q("trk_bangla_p1_ch_6", "trk_bangla", 1, 2, "১৯৫২ সালে অনশনকালে বঙ্গবন্ধুকে কোন জেলে স্থানান্তর করা হয়েছিল?", "রাজশাহী জেল", "ফরিদপুর জেল", "কুমিল্লা জেল", "চট্টগ্রাম জেল", 1, "ঢাকা কেন্দ্রীয় কারাগার থেকে বঙ্গবন্ধুকে মহিউদ্দিন আহমদসহ ফরিদপুর জেলে পাঠানো হয়।"),
            q("trk_bangla_p1_ch_6", "trk_bangla", 1, 3, "বঙ্গবন্ধু কত তারিখে অনশন ভঙ্গ করেছিলেন?", "২১ ফেব্রুয়ারি", "২৭ ফেব্রুয়ারি", "২৮ ফেব্রুয়ারি", "২৬ ফেব্রুয়ারি", 1, "টানা অনশনের পর মুক্তি আদেশ পাওয়ায় ২৭ ফেব্রুয়ারি মহিউদ্দিনসহ তিনি অনশন ভাঙেন।")
        ),
        "trk_bangla_p1_ch_7" to listOf(
            q("trk_bangla_p1_ch_7", "trk_bangla", 1, 1, "'রেইনকোট' গল্পের কেন্দ্রীয় চরিত্র নুরুল হুদা কোন বিষয়ের অধ্যাপক ছিলেন?", "বাংলা", "রসায়ন", "পদার্থবিজ্ঞান", "ইতিহাস", 1, "আখতারুজ্জামান ইলিয়াসের 'রেইনকোট' গল্পে নুরুল হুদা কলেজের কেমিস্ট্রি (রসায়ন) বিভাগের অধ্যাপক।"),
            q("trk_bangla_p1_ch_7", "trk_bangla", 1, 2, "গল্পের রেইনকোটটি কার ছিল?", "নুরুল হুদার", "শ্যালক মিন্টুর", "প্রিন্সিপাল আফাজ আহমদের", "আবদুস সাত্তার মৃধার", 1, "মুক্তিযোদ্ধা শ্যালক মিন্টুর রেখে যাওয়া রেইনকোটটি পরেই নুরুল হুদা উষ্ণ ও সাহসী বোধ করেন।"),
            q("trk_bangla_p1_ch_7", "trk_bangla", 1, 3, "কলেজের কোন অবকাঠামোটি মুক্তিযোদ্ধারা উড়িয়ে দিয়েছিল?", "লাইব্রেরি", "বৈদ্যুতিক ট্রান্সফরমার", "প্রিন্সিপালের কক্ষ", "ল্যাবরেটরি", 1, "কলেজের গেটের সামনে থাকা বৈদ্যুতিক ট্রান্সফরমারটি গেরিলা মুক্তিযোদ্ধারা ধ্বংস করে।")
        ),
        "trk_bangla_p1_ch_8" to listOf(
            q("trk_bangla_p1_ch_8", "trk_bangla", 1, 1, "'সোনার তরী' কবিতাটি রবীন্দ্রনাথের কোন কাব্যগ্রন্থের অন্তর্গত?", "চিত্রা", "সোনার তরী", "মানসী", "কল্পনা", 1, "১২৯৮ বঙ্গাব্দে রচিত কবিতাটি 'সোনার তরী' কাব্যগ্রন্থের নামকবিতা।"),
            q("trk_bangla_p1_ch_8", "trk_bangla", 1, 2, "'সোনার তরী' কবিতায় 'ঠাঁই নাই, ঠাঁই নাই— ছোট সে তরী' বলতে কী বোঝানো হয়েছে?", "নৌকা ভাঙা", "মহাকালের নৌকায় সৃষ্টির স্থান হয়, ব্যক্তির নয়", "ঝড় এসেছে", "মাঝি রাগান্বিত", 1, "মহাকালের স্রোতে মানুষের কর্মফল (সোনার ধান) রক্ষিত হয়, ব্যক্তিসত্তা নয়।"),
            q("trk_bangla_p1_ch_8", "trk_bangla", 1, 3, "'সোনার তরী' কবিতাটি কোন ছন্দে রচিত?", "স্বরবৃত্ত", "মাত্রাবৃত্ত", "অক্ষরবৃত্ত", "গদ্যছন্দ", 1, "'সোনার তরী' কবিতাটি ৮+৫ মাত্রার মাত্রাবৃত্ত ছন্দে রচিত।")
        ),
        "trk_bangla_p1_ch_9" to listOf(
            q("trk_bangla_p1_ch_9", "trk_bangla", 1, 1, "কাজী নজরুল ইসলামের 'বিদ্রোহী' কবিতা প্রথম কোন পত্রিকায় প্রকাশিত হয়?", "সওগাত", "বিজলী", "মোসলেম ভারত", "ধূমকেতু", 1, "১৯২২ সালের ৬ জানুয়ারি সাপ্তাহিক 'বিজলী' পত্রিকায় কবিতাটি প্রথম প্রকাশিত হয়।"),
            q("trk_bangla_p1_ch_9", "trk_bangla", 1, 2, "'বিদ্রোহী' কবিতায় কবি নিজেকে কার ভগবান বলেছেন?", "দেবের", "বিধাতার বক্ষে", "ইন্দ্রের", "বিশ্বামিত্রের", 1, "'আমি বিধাতার বক্ষে পদাঘাত হানি/ আমি মহা ভগবান' চরণে তিনি সকল শোষণের বিরুদ্ধে বিদ্রোহ ঘোষণা করেছেন।"),
            q("trk_bangla_p1_ch_9", "trk_bangla", 1, 3, "'বিদ্রোহী' রণক্লান্ত কবি কবে শান্ত হবেন?", "পুরস্কার পেলে", "উৎপীড়িতের ক্রন্দন-রোল আকাশে বাতাসে ধ্বনিত না হলে", "বৃষ্টি নামলে", "সন্ধ্যা হলে", 1, "অত্যাচারীর খড়্গ কৃপাণ নিস্তব্ধ হলেই বিদ্রোহী কবি শান্ত হবেন।")
        ),
        "trk_bangla_p1_ch_10" to listOf(
            q("trk_bangla_p1_ch_10", "trk_bangla", 1, 1, "'প্রতিদান' কবিতাটির রচয়িতা কে?", "জীবনানন্দ দাশ", "জসীমউদ্‌দীন", "সুফিয়া কামাল", "ফররুখ আহমদ", 1, "পল্লীকবি জসীমউদ্‌দীন রচিত 'প্রতিদান' কবিতাটি তাঁর 'বালুচর' কাব্যগ্রন্থের অন্তর্গত।"),
            q("trk_bangla_p1_ch_10", "trk_bangla", 1, 2, "যে কবিকে ঘরহীন করেছে, তার জন্য কবি কী করেন?", "অভিশাপ দেন", "তার ঘর বেঁধে দেন", "গ্রাম ছাড়েন", "বিচার চান", 1, "ক্ষমা ও মহানুভবতার আদর্শে কবি অনিষ্টকারীর জন্যও মঙ্গল কামনা ও ঘর বেঁধে দেওয়ার কথা বলেন।"),
            q("trk_bangla_p1_ch_10", "trk_bangla", 1, 3, "কবির বুকে যে কবর বেঁধেছে, কবি তার বুকে কী রচনা করেন?", "অভিযোগ", "রঙিন ফুলের সোহাগ জড়ানো ফুলমালঞ্চ", "প্রাচীর", "কাঁটার বাগান", 1, "হিংসার বদলে ভালোবাসা ও ফুলের মালঞ্চ গড়ে তোলাই কবিতাটির মূল বাণী।")
        ),
        "trk_bangla_p1_ch_11" to listOf(
            q("trk_bangla_p1_ch_11", "trk_bangla", 1, 1, "'তাহারেই পড়ে মনে' কবিতায় কোন ঋতুর আগমন ঘটেছে?", "শীত", "বসন্ত", "বর্ষা", "শরৎ", 1, "প্রকৃতিতে বসন্তের সমারোহ থাকলেও কবির হৃদয়ে প্রিয় হারানোর বেদনা-শীত বিরাজ করছে।"),
            q("trk_bangla_p1_ch_11", "trk_bangla", 1, 2, "কবি সুফিয়া কামালের কার মৃত্যুতে তাঁর মন আচ্ছন্ন ছিল?", "পিতার", "স্বামী সৈয়দ নেহাল হোসেনের", "ভাইয়ের", "কন্যার", 1, "স্বামী নেহাল হোসেনের অকালমৃত্যুর শোকই কবিতাটির রচনার নেপথ্যে ছিল।"),
            q("trk_bangla_p1_ch_11", "trk_bangla", 1, 3, "'তাহারেই পড়ে মনে' কবিতাটির গঠনরীতি কী ধরনের?", "সনেট", "কথোপকথন বা সংলাপনির্ভর", "গদ্য কবিতা", "ব্যালাড", 1, "কবি ও তাঁর ভক্তের সংলাপের মধ্য দিয়ে কবিতাটি বিকশিত হয়েছে।")
        ),
        "trk_bangla_p1_ch_12" to listOf(
            q("trk_bangla_p1_ch_12", "trk_bangla", 1, 1, "'আঠারো বছর বয়স' কবিতাটি কার রচনা?", "শামসুর রাহমান", "সুকান্ত ভট্টাচার্য", "আহসান হাবীব", "আল মাহমুদ", 1, "কিশোর কবি সুকান্ত ভট্টাচার্য মাত্র একুশ বছর জীবিত থেকেই এই কালজয়ী কবিতা রচনা করেন।"),
            q("trk_bangla_p1_ch_12", "trk_bangla", 1, 2, "কবির মতে কোন বয়স মাথা নোয়াবার নয়?", "বারো বছর", "আঠারো বছর", "ত্রিশ বছর", "পঞ্চাশ বছর", 1, "আঠারো বছর বয়স অদম্য সাহস, আত্মত্যাগ ও স্পর্ধার প্রতীক।"),
            q("trk_bangla_p1_ch_12", "trk_bangla", 1, 3, "'এ বয়স জেনো ভীরু, কাপুরুষ নয়' — এটি কোন কাব্যগ্রন্থ থেকে নেওয়া?", "ঘুম নেই", "ছাড়পত্র", "পূর্বাভাস", "মিঠেকড়া", 1, "'আঠারো বছর বয়স' কবিতাটি সুকান্তের বিখ্যাত 'ছাড়পত্র' কাব্যগ্রন্থের অন্তর্গত।")
        ),
        "trk_bangla_p1_ch_13" to listOf(
            q("trk_bangla_p1_ch_13", "trk_bangla", 1, 1, "'ফেব্রুয়ারি ১৯৬৯' কবিতায় কৃষ্ণচূড়া ফুল কিসের প্রতীক?", "বসন্তের আনন্দ", "একুশের শহিদদের রক্ত ও চেতনা", "প্রকৃতির শোক", "শিল্পকলা", 1, "শামসুর রাহমানের কবিতায় কৃষ্ণচূড়া ভাষা শহিদদের আত্মত্যাগ ও গণ-অভ্যুত্থানের রক্তচেতনার প্রতীক।"),
            q("trk_bangla_p1_ch_13", "trk_bangla", 1, 2, "'আমি কিংবদন্তির কথা বলছি' কবিতায় কবি কার কথা বলেছেন?", "রাজাদের", "পূর্বপুরুষ ও সংগ্রামী মানুষের", "পৌরাণিক দেবতার", "শিল্পীদের", 1, "আবু জাফর ওবায়দুল্লাহ তাঁর কবিতায় পলিমাটি ও সংগ্রামী পূর্বপুরুষদের ইতিহাস তুলে ধরেছেন।"),
            q("trk_bangla_p1_ch_13", "trk_bangla", 1, 3, "'যে কবিতা শুনতে জানে না সে...' — পরের অংশটি কী?", "রাজা হতে পারে না", "ক্রীতদাস থেকে যাবে", "ধনী হয় না", "কবি হয় না", 1, "সত্য ও সাহসের কাব্য না শুনলে মানুষ আজীবন ক্রীতদাসই থেকে যায় বলে কবি সতর্ক করেছেন।")
        ),
        "trk_bangla_p1_ch_14" to listOf(
            q("trk_bangla_p1_ch_14", "trk_bangla", 1, 1, "সিকান্দার আবু জাফরের 'সিরাজউদ্দৌলা' নাটকটি কয় অঙ্কে বিভক্ত?", "৩ অঙ্ক", "৪ অঙ্ক", "৫ অঙ্ক", "৬ অঙ্ক", 1, "'সিরাজউদ্দৌলা' একটি চার অঙ্কবিশিষ্ট ঐতিহাসিক ট্র্যাজেডি নাটক।"),
            q("trk_bangla_p1_ch_14", "trk_bangla", 1, 2, "পলাশীর যুদ্ধ কবে সংঘটিত হয়েছিল?", "১৭৫৬ সালের ১০ এপ্রিল", "১৭৫৭ সালের ২৩ জুন", "১৭৫৭ সালের ৩ জুলাই", "১৭৬৪ সালের ২২ অক্টোবর", 1, "১৭৫৭ সালের ২৩ জুন পলাশীর আম্রকাননে নবাবের বাহিনী বিশ্বাসঘাতকতার শিকার হয়।"),
            q("trk_bangla_p1_ch_14", "trk_bangla", 1, 3, "'নবাবের বিরুদ্ধে যে-কোনো ষড়যন্ত্রের মূলে আছেন' — প্রধান চক্রান্তকারী কে ছিলেন?", "মোহনলাল", "মীরজাফর ও ঘষেটি বেগম", "মীর মদন", "সাফ্রে", 1, "মীরজাফর, ঘষেটি বেগম, জগৎশেঠ ও রায়দুর্লভদের ষড়যন্ত্রে বাংলার স্বাধীনতা সূর্য অস্তমিত হয়।")
        ),
        "trk_bangla_p1_ch_15" to listOf(
            q("trk_bangla_p1_ch_15", "trk_bangla", 1, 1, "সৈয়দ ওয়ালীউল্লাহ্ রচিত 'লালসালু' উপন্যাস কত সালে প্রথম প্রকাশিত হয়?", "১৯৪৭ সালে", "১৯৪৮ সালে", "১৯৫২ সালে", "১৯৬০ সালে", 1, "'লালসালু' উপন্যাসটি ১৯৪৮ সালে প্রথম প্রকাশিত হয়।"),
            q("trk_bangla_p1_ch_15", "trk_bangla", 1, 2, "মহব্বতনগর গ্রামে মজিদ যে কবরটিকে মাজার বানিয়েছিল, সেটি প্রকৃতপক্ষে কার কবর ছিল বলে ধারণা?", "কোনো পীরের", "এক অজ্ঞাত ব্যক্তির বা মোদাচ্ছের পীরের নামে কল্পিত কবর", "হাজী সাহেবের", "খালিকের বাবার", 1, "মজিদ নিজের অস্তিত্ব ও স্বার্থ টিকিয়ে রাখতে একটি পুরনো কবরকে 'মোদাচ্ছের পীরের মাজার' বলে প্রচার করে।"),
            q("trk_bangla_p1_ch_15", "trk_bangla", 1, 3, "মজিদের ধর্মব্যবসা ও ভন্ডামির বিরুদ্ধে কে প্রথম নিঃশব্দ প্রতিবাদ করে?", "রহিমা", "জমিলা", "আমেনা বিবি", "তাহেরের বাপ", 1, "কিশোরী জমিলা তার প্রাণবন্ত সহজাত আচরণ ও মাজারে থুতু নিক্ষেপের মাধ্যমে মজিদের কর্তৃত্বকে নাড়িয়ে দেয়।")
        ),

        // ---------------- BANGLA 2ND PAPER (12 chapters x 3) ----------------
        "trk_bangla_p2_ch_1" to listOf(
            q("trk_bangla_p2_ch_1", "trk_bangla", 2, 1, "'অধ্যক্ষ' শব্দটির সঠিক বাংলা উচ্চারণ কোনটি?", "ওদ্‌ধক্‌খো", "অদ্‌ধোক্‌খো", "ওদ্‌ধোক্‌খো", "অধ্‌যক্‌খ", 0, "আদ্য 'অ'-এর পর য-ফলা থাকলে 'অ'-এর উচ্চারণ 'ও'-এর মতো হয়: ওদ্‌ধোক্‌খো।"),
            q("trk_bangla_p2_ch_1", "trk_bangla", 2, 2, "'আহ্বান' শব্দটির প্রমিত উচ্চারণ কোনটি?", "আহ্বান", "আওভান্", "আহোভান", "আওহান", 1, "হ্‌-এর সাথে ব-ফলা থাকলে তার উচ্চারণ 'ওভ/উভ'-এর মতো হয়: আওভান্।"),
            q("trk_bangla_p2_ch_1", "trk_bangla", 2, 3, "'মাতৃভাষা' শব্দটিতে 'অ' বা 'ঋ'-কারের উচ্চারণ কীরূপ হয়?", "মাতৃভাশা", "মাত্‌ত্রিভাশা", "মাতৃভাসা", "মাতরিভাসা", 1, "ত-ফলা/ঋ-কারযুক্ত বর্ণের প্রভাবে দ্বিত্ব উচ্চারণ হয়: মাত্‌ত্রিভাশা।")
        ),
        "trk_bangla_p2_ch_2" to listOf(
            q("trk_bangla_p2_ch_2", "trk_bangla", 2, 1, "বাংলা একাডেমির প্রমিত বানান রীতি অনুযায়ী কোনটি শুদ্ধ?", "রানী", "রাণী", "রাণি", "রানি", 3, "তদ্ভব ও দেশি শব্দে ই-কার ও ন ব্যবহারের নিয়ম অনুযায়ী 'রানি' শুদ্ধ।"),
            q("trk_bangla_p2_ch_2", "trk_bangla", 2, 2, "নিচের কোন বানানগুচ্ছটি সম্পূর্ণ শুদ্ধ?", "পোস্টমাস্টার, স্টেশন", "পোষ্টমাষ্টার, ষ্টেশন", "পোস্টমাষ্টার, ষ্টেশন", "পোষ্টমাস্টার, স্টেশন", 0, "বিদেশি শব্দে 'ষ' ব্যবহার হয় না, সর্বত্র 'স' যুক্ত 'স্ট' হয়।"),
            q("trk_bangla_p2_ch_2", "trk_bangla", 2, 3, "নিচের কোন বানানটি শুদ্ধ?", "মুহুর্মুহু", "মুহূর্মুহু", "মুহুর্মূহু", "মূহুর্মুহু", 0, "'মুহুর্মুহু' শব্দে সবগুলোতে হ্রস্ব উ-কার (ু) ব্যবহৃত হয়।")
        ),
        "trk_bangla_p2_ch_3" to listOf(
            q("trk_bangla_p2_ch_3", "trk_bangla", 2, 1, "আধুনিক বাংলা ব্যাকরণে শব্দশ্রেণি কয় প্রকার?", "৫ প্রকার", "৮ প্রকার", "৭ প্রকার", "১০ প্রকার", 1, "আধুনিক বাংলা ব্যাকরণে শব্দশ্রেণি ৮ প্রকার: বিশেষ্য, সর্বনাম, বিশেষণ, ক্রিয়া, ক্রিয়াবিশেষণ, অনুসর্গ, যোজক, আবেগ।"),
            q("trk_bangla_p2_ch_3", "trk_bangla", 2, 2, "'সাদা কাগজ' — এখানে 'সাদা' কোন পদ/শব্দশ্রেণি?", "বিশেষ্য", "বিশেষণ", "সর্বনাম", "অব্যয়", 1, "'সাদা' শব্দটি 'কাগজ' বিশেষ্যের গুণ বা রঙ প্রকাশ করায় এটি বিশেষণ।"),
            q("trk_bangla_p2_ch_3", "trk_bangla", 2, 3, "'শাবাশ! দারুণ খেলেছ।' — 'শাবাশ' কোন শব্দশ্রেণি?", "যোজক", "আবেগ শব্দ", "অনুসর্গ", "ক্রিয়াবিশেষণ", 1, "প্রশংসা বা মনের আবেগ প্রকাশ করায় এটি আবেগ শব্দ।")
        ),
        "trk_bangla_p2_ch_4" to listOf(
            q("trk_bangla_p2_ch_4", "trk_bangla", 2, 1, "'চৌরাস্তা' কোন সমাসের উদাহরণ?", "দ্বন্দ্ব", "দ্বিগু", "বহুব্রীহি", "অব্যয়ীভাব", 1, "'চৌ (চার) রাস্তার সমাহার' — সংখ্যাবাচক শব্দ পূর্বে থাকায় এটি দ্বিগু সমাস।"),
            q("trk_bangla_p2_ch_4", "trk_bangla", 2, 2, "'উপকূল' কোন সমাস?", "কর্মধারয়", "অব্যয়ীভাব", "তৎপুরুষ", "দ্বিগু", 1, "সামীপ্য অর্থে 'উপ' অব্যয়ের সাথে যুক্ত হয়ে 'কূলের সমীপে' = উপকূল (অব্যয়ীভাব)।"),
            q("trk_bangla_p2_ch_4", "trk_bangla", 2, 3, "যে পদের অর্থ প্রধান না হয়ে অন্য পদের অর্থ প্রধান হয়, তাকে কোন সমাস বলে?", "দ্বন্দ্ব", "বহুব্রীহি", "তৎপুরুষ", "কর্মধারয়", 1, "যেমন 'নীলকণ্ঠ' (নীল কণ্ঠ যার = শিব), এখানে তৃতীয় অর্থ প্রধান—এটি বহুব্রীহি সমাস।")
        ),
        "trk_bangla_p2_ch_5" to listOf(
            q("trk_bangla_p2_ch_5", "trk_bangla", 2, 1, "একটি সার্থক বাক্যের কয়টি গুণ থাকা আবশ্যক?", "২টি", "৩টি", "৪টি", "৫টি", 1, "সার্থক বাক্যের ৩টি গুণ: আকাঙ্ক্ষা, আসত্তি ও যোগ্যতা।"),
            q("trk_bangla_p2_ch_5", "trk_bangla", 2, 2, "'গরু আকাশে ওড়ে' — বাক্যটিতে কোন গুণের অভাব রয়েছে?", "আকাঙ্ক্ষা", "যোগ্যতা", "আসত্তি", "শূন্যতা", 1, "পদগুলোর অর্থগত ও ভাবগত মিল না থাকায় এটি যোগ্যতাহীন বাক্য।"),
            q("trk_bangla_p2_ch_5", "trk_bangla", 2, 3, "'যদি পরিশ্রম কর, তবে সফল হবে' — গঠন অনুসারে এটি কোন বাক্য?", "সরল বাক্য", "জটিল বা মিশ্র বাক্য", "যৌগিক বাক্য", "খণ্ড বাক্য", 1, "সাপেক্ষ সর্বনাম 'যদি...তবে' দ্বারা প্রধান ও আশ্রিত বাক্যাংশ যুক্ত হওয়ায় এটি জটিল বাক্য।")
        ),
        "trk_bangla_p2_ch_6" to listOf(
            q("trk_bangla_p2_ch_6", "trk_bangla", 2, 1, "নিচের কোন বাক্যটি শুদ্ধ?", "সকল ছাত্ররা উপস্থিত ছিল", "সব ছাত্র উপস্থিত ছিল", "সকল ছাত্রবৃন্দ উপস্থিত ছিল", "সব ছাত্রগণ উপস্থিত ছিল", 1, "'সব' ও '-রা/-বৃন্দ' একত্রে বহুবচন বাহুল্য দোষ ঘটায়; তাই 'সব ছাত্র' শুদ্ধ।"),
            q("trk_bangla_p2_ch_6", "trk_bangla", 2, 2, "'কেবলমাত্র তারাই যাবে' বাক্যের শুদ্ধ রূপ কী?", "কেবল তারাই যাবে", "শুধু মাত্র তারাই যাবে", "একাত্র তারাই যাবে", "কেবলমাত্র তারাই যায়", 0, "'কেবল' ও 'মাত্র' একই অর্থে ব্যবহৃত হওয়ায় দ্বিত্ব বাহুল্য ঘটে; যেকোনো একটি রাখতে হবে।"),
            q("trk_bangla_p2_ch_6", "trk_bangla", 2, 3, "নিচের কোন শব্দটি অশুদ্ধ নয় (শুদ্ধ)?", "উৎকর্ষতা", "উৎকর্ষ", "সৌজন্যতা", "কার্পণ্যতা", 1, "'উৎকর্ষ' নিজেই ভাববাচক বিশেষ্য; এর সাথে '-তা' প্রত্যয় যুক্ত হয় না।")
        ),
        "trk_bangla_p2_ch_7" to listOf(
            q("trk_bangla_p2_ch_7", "trk_bangla", 2, 1, "'Manifesto' শব্দের বাংলা পরিভাষা কী?", "প্রজ্ঞাপন", "ইশতেহার", "কার্যবিবরণী", "সংবিধান", 1, "রাজনৈতিক বা নীতিগত ঘোষণাপত্রকে বাংলায় 'ইশতেহার' বলা হয়।"),
            q("trk_bangla_p2_ch_7", "trk_bangla", 2, 2, "'Agenda' শব্দের প্রচলিত বাংলা পারিভাষিক রূপ কোনটি?", "উপসংহার", "আলোচ্যসূচি", "ঘোষণাপত্র", "অনুমোদন", 1, "সভার আলোচ্য বিষয়ের তালিকাকে 'আলোচ্যসূচি' বলে।"),
            q("trk_bangla_p2_ch_7", "trk_bangla", 2, 3, "'Subsidy' শব্দের সঠিক বাংলা পরিভাষা কী?", "শুল্ক", "ভর্তুকি", "আয়কর", "বাজেট", 1, "সরকার প্রদত্ত আর্থিক সহায়তা বা 'Subsidy'-এর বাংলা পরিভাষা 'ভর্তুকি'।")
        ),
        "trk_bangla_p2_ch_8" to listOf(
            q("trk_bangla_p2_ch_8", "trk_bangla", 2, 1, "প্রতিবেদন রচনায় সবচেয়ে গুরুত্বপূর্ণ দিক কোনটি?", "কাব্যিক ভাষা", "সত্যনিষ্ঠা, তথ্য ও নিরপেক্ষতা", "কাল্পনিক বর্ণনা", "অতিরিক্ত আবেগ", 1, "প্রতিবেদন অবশ্যই বস্তুনিষ্ঠ, তথ্যবহুল ও পক্ষপাতহীন হতে হয়।"),
            q("trk_bangla_p2_ch_8", "trk_bangla", 2, 2, "দিনলিপি লেখার সময় অবশ্যই কী উল্লেখ করতে হয়?", "মূল্য", "তারিখ, বার ও সময়", "শিরোনামের সূচিপত্র", "স্বাক্ষরের সিল", 1, "প্রতিদিনের ব্যক্তিগত অভিজ্ঞতা তারিখ ও বার উল্লেখ করে লিখতে হয়।"),
            q("trk_bangla_p2_ch_8", "trk_bangla", 2, 3, "সংবাদপত্রে প্রকাশের উপযোগী প্রতিবেদনে কোন অংশটি আবশ্যিক?", "অভিনন্দন", "আকর্ষণীয় শিরোনাম ও ঘটনার বিবরণ", "উৎসর্গপত্র", "গ্রন্থপঞ্জি", 1, "সংবাদ প্রতিবেদন প্রেরকের ঠিকানা ও শিরোনামসহ সংক্ষিপ্ত ও তথ্যপূর্ণ হয়।")
        ),
        "trk_bangla_p2_ch_9" to listOf(
            q("trk_bangla_p2_ch_9", "trk_bangla", 2, 1, "বৈদ্যুতিন চিঠিতে (E-mail) মূল ঠিকানার ঘরে কী লেখা হয়?", "মোবাইল নম্বর", "প্রাপকের ইমেইল অ্যাড্রেস (To)", "পোস্ট কোড", "ডাকটিকিট", 1, "ইমেইলের 'To' ফিল্ডে প্রাপকের সঠিক ইমেইল ঠিকানা লিখতে হয়।"),
            q("trk_bangla_p2_ch_9", "trk_bangla", 2, 2, "প্রাতিষ্ঠানিক আবেদনপত্রের কয়টি অংশ থাকে?", "৩টি", "৬টি", "২টি", "৪টি", 1, "আবেদনপত্রে তারিখ ও প্রাপক, বিষয়, সম্বোধন, মূল বক্তব্য, বিদায় সম্ভাষণ ও স্বাক্ষর—প্রধান ৬টি অংশ থাকে।"),
            q("trk_bangla_p2_ch_9", "trk_bangla", 2, 3, "ইমেইলে একই বার্তার অনুলিপি অন্য কাউকে পাঠাতে কী ব্যবহৃত হয়?", "URL", "CC / BCC", "DNS", "HTTP", 1, "CC (Carbon Copy) বা BCC (Blind Carbon Copy) ব্যবহার করে একাধিক প্রাপককে অনুলিপি পাঠানো যায়।")
        ),
        "trk_bangla_p2_ch_10" to listOf(
            q("trk_bangla_p2_ch_10", "trk_bangla", 2, 1, "সারাংশ বা সারমর্ম মূল অনুচ্ছেদের কত অংশের মধ্যে সীমাবদ্ধ রাখা উত্তম?", "এক-তৃতীয়াংশ", "দ্বিগুণ", "সমান", "পাঁচ ভাগের চার ভাগ", 0, "সারাংশ মূল রচনার সংক্ষিপ্ত নির্যাস, যা সাধারণত এক-তৃতীয়াংশ আয়তনের হয়।"),
            q("trk_bangla_p2_ch_10", "trk_bangla", 2, 2, "ভাব-সম্প্রসারণ রচনায় কী বর্জন করা উচিত?", "সহজ ভাষা", "একই কথার পুনরাবৃত্তি ও উদ্ধৃতির বাহুল্য", "মূল ভাব বিশ্লেষণ", "যৌক্তিক প্রসারণ", 1, "ভাব-সম্প্রসারণ অনুচ্ছেদ আকারে সাজিয়ে স্পষ্ট যুক্তিতে লিখতে হয়, পুনরুক্তি বর্জনীয়।"),
            q("trk_bangla_p2_ch_10", "trk_bangla", 2, 3, "গদ্যের মূলভাব সংক্ষেপে লেখা হলে তাকে বলে সারাংশ; কবিতার মূলভাব সংক্ষেপে লিখলে কী বলে?", "ভাবার্থ", "সারমর্ম", "রচনা", "উপসংহার", 1, "কবিতা বা পদ্যাংশের অন্তর্নিহিত ভাবের সংক্ষিপ্ত প্রকাশই সারমর্ম।")
        ),
        "trk_bangla_p2_ch_11" to listOf(
            q("trk_bangla_p2_ch_11", "trk_bangla", 2, 1, "সংলাপ রচনায় কোন রীতির ভাষা ব্যবহার সবচেয়ে স্বাভাবিক?", "তৎসমবহুল সাধু রীতি", "প্রমিত চলিত ও স্বাভাবিক কথ্য রীতি", "অতি জটিল বাক্য", "একই বাক্যের পুনরাবৃত্তি", 1, "দুই বা ততোধিক চরিত্রের কথোপকথন প্রমিত চলিত রীতিতে প্রাণবন্ত হয়।"),
            q("trk_bangla_p2_ch_11", "trk_bangla", 2, 2, "ক্ষুদে গল্পের প্রধান বৈশিষ্ট্য কী?", "দীর্ঘ ইতিহাস বর্ণনা", "অল্প পরিসরে চমকপ্রদ ও তীক্ষ্ণ সমাপ্তি", "প্রবন্ধের মতো তথ্য সারণি", "নাটকের দৃশ্য বিভাজন", 1, "ক্ষুদে গল্প সংক্ষিপ্ত অবয়বে একটি মানবিক বা জীবনঘনিষ্ঠ সত্যকে ফুটিয়ে তোলে।"),
            q("trk_bangla_p2_ch_11", "trk_bangla", 2, 3, "ক্ষুদে গল্পের প্রথমে কী দেওয়া আবশ্যিক?", "উৎসর্গ", "মানানসই ও আকর্ষণীয় শিরোনাম", "সূচিপত্র", "গ্রন্থপঞ্জি", 1, "গল্পের মূলভাবের সাথে সামঞ্জস্যপূর্ণ শিরোনাম দেওয়া জরুরি।")
        ),
        "trk_bangla_p2_ch_12" to listOf(
            q("trk_bangla_p2_ch_12", "trk_bangla", 2, 1, "একটি আদর্শ প্রবন্ধ-নিবন্ধের কয়টি প্রধান অংশ থাকে?", "২টি", "৩টি (ভূমিকা, মূল আলোচনা, উপসংহার)", "১টি", "৫টি", 1, "ভূমিকা, ধারাবাহিক অনুচ্ছেদসমৃদ্ধ মূল বক্তব্য ও উপসংহার—এই ৩টি প্রধান অংশে প্রবন্ধ গঠিত হয়।"),
            q("trk_bangla_p2_ch_12", "trk_bangla", 2, 2, "'প্রবন্ধ' শব্দটির আক্ষরিক অর্থ কী?", "ছোট কবিতা", "বিশেষভাবে বাঁধা বা প্রকৃষ্টরূপে বন্ধন", "নাটকের সংলাপ", "গোপন পত্র", 1, "'প্র + বন্দ্ + অ' = প্রবন্ধ, অর্থাৎ যুক্তিনির্ভর ও সুশৃঙ্খল গদ্য রচনা।"),
            q("trk_bangla_p2_ch_12", "trk_bangla", 2, 3, "প্রবন্ধ রচনার মান সমৃদ্ধ করতে কোনটি সহায়ক?", "অপ্রাসঙ্গিক গল্প", "সঠিক তথ্য, উপাত্ত ও প্রাসঙ্গিক উদ্ধৃতি", "বানান ভুল", "একই অনুচ্ছেদে সব লেখা", 1, "যথাযথ পরিসংখ্যান, যুক্তি ও প্যারাগ্রাফ বিন্যাস প্রবন্ধকে প্রাঞ্জল ও প্রামাণিক করে।")
        ),

        // ---------------- ENGLISH 1ST PAPER (12 chapters x 3) ----------------
        "trk_english_p1_ch_1" to listOf(
            q("trk_english_p1_ch_1", "trk_english", 1, 1, "On which date did Bangabandhu deliver the historic 7th March Speech at Race Course Maidan?", "25 March 1971", "7 March 1971", "16 December 1971", "26 March 1971", 1, "Bangabandhu Sheikh Mujibur Rahman delivered the landmark speech on 7 March 1971."),
            q("trk_english_p1_ch_1", "trk_english", 1, 2, "What does 'apartheid' refer to in Nelson Mandela's struggle?", "Economic recession", "Institutionalized racial segregation in South Africa", "Climate treaty", "Literacy drive", 1, "Apartheid was South Africa's policy of racial discrimination against non-white citizens."),
            q("trk_english_p1_ch_1", "trk_english", 1, 3, "How long was Nelson Mandela imprisoned before becoming South Africa's president?", "10 years", "27 years", "15 years", "5 years", 1, "Mandela spent nearly three decades (27 years) in prison fighting apartheid.")
        ),
        "trk_english_p1_ch_2" to listOf(
            q("trk_english_p1_ch_2", "trk_english", 1, 1, "According to the HSC unit 'Education and Life', true education aims to develop:", "Only rote memorization", "Civic values, critical thinking, and compassion", "Silence in class", "Wealth hoarding", 1, "Education nurtures reasoning, empathy, and ability to serve humanity."),
            q("trk_english_p1_ch_2", "trk_english", 1, 2, "The word 'enlighten' most closely means:", "Darken", "Educate and broaden understanding", "Forget", "Ignore", 1, "To enlighten means to give greater knowledge, insight, and wisdom."),
            q("trk_english_p1_ch_2", "trk_english", 1, 3, "Which skill is considered vital in 21st-century civic engagement?", "Superstition", "Critical inquiry and empathy", "Isolation", "Copying", 1, "Modern education prioritizes critical inquiry and active citizenship.")
        ),
        "trk_english_p1_ch_3" to listOf(
            q("trk_english_p1_ch_3", "trk_english", 1, 1, "Who wrote the famous 'I Have a Dream' speech?", "Langston Hughes", "Martin Luther King Jr.", "D.H. Lawrence", "W.B. Yeats", 1, "Martin Luther King Jr. delivered 'I Have a Dream' in 1963 for racial justice."),
            q("trk_english_p1_ch_3", "trk_english", 1, 2, "In D.H. Lawrence's poem 'Dreams', what distinguishes dreamers of the day?", "They forget everything", "They act with open eyes to make dreams real", "They only sleep", "They fear daylight", 1, "Lawrence calls day-dreamers dangerous because they actively turn visions into reality."),
            q("trk_english_p1_ch_3", "trk_english", 1, 3, "In Langston Hughes' 'Dreams', life without dreams is compared to:", "A flying eagle", "A broken-winged bird that cannot fly", "A calm sea", "A bright star", 1, "Hughes warns: 'Hold fast to dreams, for if dreams die, life is a broken-winged bird.'")
        ),
        "trk_english_p1_ch_4" to listOf(
            q("trk_english_p1_ch_4", "trk_english", 1, 1, "In the HSC text, Sheikh Kamal was a co-founder of which pioneering sports club?", "Mohammedan Sporting Club", "Abahani Krira Chakra", "Brothers Union", "Victoria SC", 1, "Sheikh Kamal founded Abahani Krira Chakra and modernized football in Bangladesh."),
            q("trk_english_p1_ch_4", "trk_english", 1, 2, "Brojen Das earned international fame for:", "Mountaineering", "Crossing the English Channel first among Asians", "Chess", "Cricket", 1, "Bangladeshi swimmer Brojen Das became the first Asian to swim the English Channel."),
            q("trk_english_p1_ch_4", "trk_english", 1, 3, "Nishat Mazumder is celebrated as the first Bangladeshi woman to:", "Fly a glider", "Climb Mount Everest", "Sail across the Pacific", "Win a Nobel Prize", 1, "Nishat Mazumder scaled Mount Everest in May 2012.")
        ),
        "trk_english_p1_ch_5" to listOf(
            q("trk_english_p1_ch_5", "trk_english", 1, 1, "What is the central theme of 'Love and Friendship' in HSC Unit 5?", "Material wealth", "Unconditional care, trust, and mutual respect", "Strict competition", "Isolation", 1, "Healthy relationships thrive on honesty, respect, and emotional support."),
            q("trk_english_p1_ch_5", "trk_english", 1, 2, "The noun form of 'affectionate' is:", "Affection", "Affecting", "Affectionately", "Affect", 0, "'Affection' is the noun form meaning gentle fondness or care."),
            q("trk_english_p1_ch_5", "trk_english", 1, 3, "In 'The Luncheon' by W. Somerset Maugham, what did the lady claim she never ate?", "Asparagus", "More than one thing for luncheon", "Champagne", "Caviar", 1, "Humorously, the guest kept saying she never ate more than one thing while ordering lavishly.")
        ),
        "trk_english_p1_ch_6" to listOf(
            q("trk_english_p1_ch_6", "trk_english", 1, 1, "According to WHO, adolescence generally spans which age group?", "5–9 years", "10–19 years", "21–30 years", "2–6 years", 1, "WHO defines adolescents as young people between 10 and 19 years of age."),
            q("trk_english_p1_ch_6", "trk_english", 1, 2, "In Shilpi's story, early marriage and early pregnancy are shown to cause:", "Higher income", "Serious maternal health risks and loss of schooling", "Better fitness", "Stronger immunity", 1, "Early pregnancy threatens adolescent mothers' lives and interrupts education."),
            q("trk_english_p1_ch_6", "trk_english", 1, 3, "Which factor best empowers adolescent girls like Shilpi?", "Peer pressure", "Education, health awareness, and family support", "Silence", "Isolation", 1, "Education and community counseling helped Shilpi convince her family and village.")
        ),
        "trk_english_p1_ch_7" to listOf(
            q("trk_english_p1_ch_7", "trk_english", 1, 1, "What does 'food adulteration' mean?", "Cooking organically", "Adding unsafe or inferior substances to food", "Freezing vegetables", "Packaging cleanly", 1, "Food adulteration involves mixing harmful chemicals like formalin or artificial dyes."),
            q("trk_english_p1_ch_7", "trk_english", 1, 2, "In 'Spending', overspending beyond means commonly leads to:", "Peace of mind", "Financial stress and debt", "Automatic savings", "Higher credit score", 1, "Wise budgeting prevents consumer debt and financial anxiety."),
            q("trk_english_p1_ch_7", "trk_english", 1, 3, "The antonym of 'contaminated' is:", "Polluted", "Pure / uncontaminated", "Toxic", "Adulterated", 1, "'Pure' means free from harmful contaminants.")
        ),
        "trk_english_p1_ch_8" to listOf(
            q("trk_english_p1_ch_8", "trk_english", 1, 1, "According to the UN Universal Declaration of Human Rights (1948), all human beings are born:", "Ranked by wealth", "Free and equal in dignity and rights", "Subordinate to states", "Identical in talent", 1, "Article 1 of the UDHR declares every human free and equal in dignity and rights."),
            q("trk_english_p1_ch_8", "trk_english", 1, 2, "Amerigo in 'Amerigo, a Street Child' earned a living by:", "Working in a bank", "Collecting trash, selling ice cream, and shining shoes", "Driving buses", "Teaching tuition", 1, "Abandoned street child Amerigo sorted trash and did hazardous odd jobs to survive."),
            q("trk_english_p1_ch_8", "trk_english", 1, 3, "Why did Amerigo say he was 'lucky' not to get sick while picking trash?", "Medical care was free", "His friend died from trash-sorting infection", "He had insurance", "He liked cold nights", 1, "His friend died after a painful infection from sorting rubbish, highlighting children's vulnerability.")
        ),
        "trk_english_p1_ch_9" to listOf(
            q("trk_english_p1_ch_9", "trk_english", 1, 1, "What is 'ecotourism'?", "Luxury casino travel", "Responsible travel that conserves nature and supports local people", "Industrial mining", "High-carbon cruises", 1, "Ecotourism minimizes ecological impact and empowers local communities."),
            q("trk_english_p1_ch_9", "trk_english", 1, 2, "Kuakata in southern Bangladesh is uniquely known as 'Sagar Kannya' because:", "It has snow", "Both sunrise and sunset can be seen over the sea", "It is an island", "It has volcanoes", 1, "Kuakata offers unobstructed views of both sunrise and sunset over the Bay of Bengal."),
            q("trk_english_p1_ch_9", "trk_english", 1, 3, "Which mangrove forest is a UNESCO World Heritage Site in Bangladesh?", "Ratargul", "The Sundarbans", "Lawachara", "Modhupur", 1, "The Sundarbans is the world's largest mangrove forest and a UNESCO World Heritage Site.")
        ),
        "trk_english_p1_ch_10" to listOf(
            q("trk_english_p1_ch_10", "trk_english", 1, 1, "The Hakaluki Haor in Bangladesh is ecologically famous as:", "A desert", "A rich wetland supporting biodiversity and migratory birds", "A coral reef", "An ice glacier", 1, "Hakaluki Haor is Bangladesh's largest freshwater wetland ecosystem."),
            q("trk_english_p1_ch_10", "trk_english", 1, 2, "What is the main threat to Bangladesh's tigers in the Sundarbans?", "Cold wind", "Poaching, habitat loss, and salinity rise", "Too many rivers", "Tourism hotels", 1, "Habitat shrinking, cyclones, and poaching threaten the Royal Bengal Tiger."),
            q("trk_english_p1_ch_10", "trk_english", 1, 3, "'Biodiversity' refers to:", "Single crop farming", "The variety of plant and animal life in an ecosystem", "Concrete roads", "Plastic waste", 1, "Biodiversity measures the richness of species within nature.")
        ),
        "trk_english_p1_ch_11" to listOf(
            q("trk_english_p1_ch_11", "trk_english", 1, 1, "Who painted the iconic 'Famine Sketches' of 1943 in Bengal?", "S.M. Sultan", "Zainul Abedin", "Quamrul Hassan", "Novera Ahmed", 1, "Shilpacharya Zainul Abedin's 1943 famine sketches brought global acclaim."),
            q("trk_english_p1_ch_11", "trk_english", 1, 2, "What is 'Nakshi Kantha'?", "Wooden sculpture", "Traditional embroidered quilt art of rural Bengal", "Clay pottery", "Bamboo flute", 1, "Nakshi Kantha is an intricate folk embroidery tradition made from layered old saris."),
            q("trk_english_p1_ch_11", "trk_english", 1, 3, "Who established the Folk Arts and Crafts Museum at Sonargaon?", "Quamrul Hassan", "Zainul Abedin", "Jasimuddin", "Hashem Khan", 1, "Zainul Abedin founded the Folk Arts and Crafts Museum in Sonargaon in 1975.")
        ),
        "trk_english_p1_ch_12" to listOf(
            q("trk_english_p1_ch_12", "trk_english", 1, 1, "In Greek mythology, who possessed superhuman strength and performed Twelve Labours?", "Icarus", "Hercules", "Odysseus", "Achilles", 1, "Hercules completed twelve heroic tasks including slaying the Nemean Lion."),
            q("trk_english_p1_ch_12", "trk_english", 1, 2, "Behula's story in Bengali folklore highlights:", "Betrayal", "Courageous devotion that revived her husband Lakhindar", "Cowardice", "Greed", 1, "Behula braved treacherous waters on a raft to restore her husband's life."),
            q("trk_english_p1_ch_12", "trk_english", 1, 3, "What does the phrase 'Achilles' heel' signify?", "Swift running", "A fatal hidden weakness despite overall strength", "Iron armor", "Victory", 1, "Named after Greek warrior Achilles, it denotes a person's one vulnerable spot.")
        ),

        // ---------------- ENGLISH 2ND PAPER (12 chapters x 3) ----------------
        "trk_english_p2_ch_1" to listOf(
            q("trk_english_p2_ch_1", "trk_english", 2, 1, "He is proficient ___ English and Mathematics.", "at", "in", "on", "with", 1, "The adjective 'proficient' takes the preposition 'in' (skilled in a subject)."),
            q("trk_english_p2_ch_1", "trk_english", 2, 2, "Do not hanker ___ wealth; focus on knowledge.", "for", "after", "on", "with", 1, "'Hanker after' is the correct idiomatic phrase meaning crave/desire intensely."),
            q("trk_english_p2_ch_1", "trk_english", 2, 3, "We must abide ___ the rules of the examination hall.", "with", "by", "to", "in", 1, "'Abide by' means to obey or follow a rule or agreement.")
        ),
        "trk_english_p2_ch_2" to listOf(
            q("trk_english_p2_ch_2", "trk_english", 2, 1, "You ___ study harder if you want to get GPA 5.0.", "had better", "would rather", "as soon as", "no sooner", 0, "'Had better' expresses strong advice or obligation followed by base verb."),
            q("trk_english_p2_ch_2", "trk_english", 2, 2, "___ I reached the station than the train left.", "Hardly had", "No sooner had", "Scarcely had", "As soon as", 1, "'No sooner had ... than' pairs strictly with 'than'."),
            q("trk_english_p2_ch_2", "trk_english", 2, 3, "He talks ___ he knew everything.", "so that", "as if", "lest", "unless", 1, "'As if / as though' introduces an unreal comparison (followed by past subjunctive).")
        ),
        "trk_english_p2_ch_3" to listOf(
            q("trk_english_p2_ch_3", "trk_english", 2, 1, "Walk carefully lest you ___.", "fell", "should fall", "will fall", "are falling", 1, "'Lest' is always followed by 'should + base verb' (never 'not')."),
            q("trk_english_p2_ch_3", "trk_english", 2, 2, "It is high time we ___ our bad habits.", "change", "changed", "will change", "changing", 1, "'It is high time + subject' requires the past simple tense ('changed')."),
            q("trk_english_p2_ch_3", "trk_english", 2, 3, "Had I the wings of a bird, I ___ fly to you.", "will", "would", "can", "shall", 1, "Second conditional inversion 'Had I...' takes 'would + base verb' in the main clause.")
        ),
        "trk_english_p2_ch_4" to listOf(
            q("trk_english_p2_ch_4", "trk_english", 2, 1, "Neither the teacher nor the students ___ present yesterday.", "was", "were", "is", "has", 1, "With 'Neither...nor', the verb agrees with the nearer noun ('students' -> 'were')."),
            q("trk_english_p2_ch_4", "trk_english", 2, 2, "I look forward to ___ from you soon.", "hear", "hearing", "heard", "be heard", 1, "The phrase 'look forward to' ends in preposition 'to', so it takes a gerund ('hearing')."),
            q("trk_english_p2_ch_4", "trk_english", 2, 3, "Fifty miles ___ a long distance to walk.", "are", "is", "were", "have", 1, "A fixed measurement, sum, or distance acting as a single unit takes a singular verb ('is').")
        ),
        "trk_english_p2_ch_5" to listOf(
            q("trk_english_p2_ch_5", "trk_english", 2, 1, "Indirect speech of: He said, \"I am reading a novel.\"", "He said he reads a novel.", "He said that he was reading a novel.", "He told he is reading a novel.", "He said that I was reading a novel.", 1, "Present continuous in direct speech changes to past continuous in reported speech."),
            q("trk_english_p2_ch_5", "trk_english", 2, 2, "The teacher said, \"Honesty is the best policy.\"", "The teacher said that honesty was the best policy.", "The teacher said that honesty is the best policy.", "The teacher told honesty was good.", "The teacher asked if honesty is good.", 1, "Universal truths remain in the present simple tense in indirect narration."),
            q("trk_english_p2_ch_5", "trk_english", 2, 3, "Simple form of: \"Though he is poor, he is honest.\"", "He is poor and honest.", "In spite of being poor, he is honest.", "Because he is poor, he is honest.", "Unless he is poor, he is honest.", 1, "'In spite of / Despite + noun/gerund' converts a concessive complex sentence into simple.")
        ),
        "trk_english_p2_ch_6" to listOf(
            q("trk_english_p2_ch_6", "trk_english", 2, 1, "In 'A barking dog seldom bites', the word 'barking' is a:", "Gerund", "Present participle (pre-modifier)", "Infinitive", "Appositive", 1, "Acting like an adjective modifying 'dog', 'barking' is a present participle pre-modifier."),
            q("trk_english_p2_ch_6", "trk_bangla", 2, 2, "Wait — identify the appositive in: 'Kazi Nazrul Islam, our national poet, wrote Bidrohi.'", "Kazi Nazrul Islam", "our national poet", "wrote", "Bidrohi", 1, "An appositive ('our national poet') renames or clarifies the noun beside it.")
                .copy(subjectId = "trk_english"),
            q("trk_english_p2_ch_6", "trk_english", 2, 3, "'He ran very fast.' — Here 'very' modifies which part of speech?", "Noun", "Adverb ('fast')", "Verb ('ran')", "Pronoun", 1, "'Very' functions as an intensifier modifying the adverb 'fast'.")
        ),
        "trk_english_p2_ch_7" to listOf(
            q("trk_english_p2_ch_7", "trk_english", 2, 1, "He worked hard; ___, he stood first in the HSC exam.", "however", "consequently / therefore", "on the contrary", "otherwise", 1, "'Consequently / therefore' connects cause to logical positive outcome."),
            q("trk_english_p2_ch_7", "trk_english", 2, 2, "Read regularly, ___ you will fail to cover the syllabus.", "moreover", "otherwise", "likewise", "in addition", 1, "'Otherwise' introduces the negative consequence of not doing the first action."),
            q("trk_english_p2_ch_7", "trk_english", 2, 3, "Mobile phones connect people instantly. ___, excessive screen use harms focus.", "Therefore", "However / On the other hand", "First of all", "For example", 1, "'However' signals contrast between a benefit and a drawback.")
        ),
        "trk_english_p2_ch_8" to listOf(
            q("trk_english_p2_ch_8", "trk_english", 2, 1, "Choose the closest synonym of 'Diligent':", "Lazy", "Hardworking / Industrious", "Careless", "Fragile", 1, "'Diligent' means steady, earnest, and hardworking."),
            q("trk_english_p2_ch_8", "trk_english", 2, 2, "Choose the antonym of 'Benevolent':", "Kind", "Malevolent / Cruel", "Generous", "Helpful", 1, "'Benevolent' (kindly) contrasts directly with 'malevolent' (wishing harm)."),
            q("trk_english_p2_ch_8", "trk_english", 2, 3, "Choose the synonym of 'Ephemeral':", "Permanent", "Short-lived / Transient", "Heavy", "Ancient", 1, "'Ephemeral' refers to something lasting only a very short time.")
        ),
        "trk_english_p2_ch_9" to listOf(
            q("trk_english_p2_ch_9", "trk_english", 2, 1, "Which sentence is punctuated correctly?", "Hurrah we have won.", "Hurrah! We have won.", "Hurrah? we have won!", "Hurrah, we have won?", 1, "Interjections like 'Hurrah!' take an exclamation mark followed by a capitalized sentence."),
            q("trk_english_p2_ch_9", "trk_english", 2, 2, "What punctuation joins two closely related independent clauses without a conjunction?", "Comma alone", "Semicolon (;)", "Hyphen (-)", "Apostrophe (')", 1, "A semicolon links two closely related independent clauses without 'and/but'."),
            q("trk_english_p2_ch_9", "trk_english", 2, 3, "Choose the correct possessive form for plural 'students':", "student's books", "students' books", "students's book", "students books", 1, "For plural nouns ending in 's', the apostrophe comes after the 's': students'.")
        ),
        "trk_english_p2_ch_10" to listOf(
            q("trk_english_p2_ch_10", "trk_english", 2, 1, "In a formal application to the Principal, the standard salutation is:", "Hey buddy,", "Sir / Madam,", "Dear Uncle,", "Hi Principal,", 1, "Formal letters require respectful salutations like 'Sir / Madam' or 'Respected Principal'."),
            q("trk_english_p2_ch_10", "trk_english", 2, 2, "If a formal letter begins with 'Dear Sir/Madam', how should the sign-off end?", "Yours lovingly,", "Yours faithfully,", "See you,", "Best bro,", 1, "'Yours faithfully' is the British/HSC standard closing when the recipient's name isn't used."),
            q("trk_english_p2_ch_10", "trk_english", 2, 3, "Where does the 'Subject' line appear in a formal application?", "After the signature", "Between recipient address/salutation and main body", "At the very bottom", "Inside the envelope only", 1, "Putting the 'Subject:' line right above the salutation/body states the purpose immediately.")
        ),
        "trk_english_p2_ch_11" to listOf(
            q("trk_english_p2_ch_11", "trk_english", 2, 1, "Which tone is required in news and academic report writing?", "Fictional and exaggerated", "Objective, factual, and clear", "Poetic rhyming", "First-person slang", 1, "Reports rely on verified facts, dates, quotes, and neutral wording."),
            q("trk_english_p2_ch_11", "trk_english", 2, 2, "A newspaper report typically starts below its headline with:", "A poem", "A byline and dateline (place & date)", "A signature block", "Multiple choice boxes", 1, "News reports open with the reporter's byline and location/date."),
            q("trk_english_p2_ch_11", "trk_english", 2, 3, "In the 'Inverted Pyramid' report structure, the most critical facts go:", "In the last paragraph", "In the opening lead paragraph", "Only in footnotes", "In the appendix", 1, "The lead summarizes Who, What, When, Where, and Why up front.")
        ),
        "trk_english_p2_ch_12" to listOf(
            q("trk_english_p2_ch_12", "trk_english", 2, 1, "How is a standard HSC paragraph structured?", "Bullet points only", "One unified block: Topic sentence, supporting details, conclusion", "Ten separate headings", "Only questions", 1, "A paragraph stays in a single cohesive block unified around one topic sentence."),
            q("trk_english_p2_ch_12", "trk_english", 2, 2, "What is the role of a 'Topic Sentence' in a paragraph?", "Gives a bibliography", "Introduces the central idea of the paragraph", "Ends the essay", "Lists numbers only", 1, "The topic sentence announces the controlling idea for the whole paragraph."),
            q("trk_english_p2_ch_12", "trk_english", 2, 3, "Unlike a single paragraph, a full 'Composition / Essay' contains:", "Only 5 lines", "Multiple paragraphs: Introduction, Body paragraphs, Conclusion", "No title", "Only dialogue", 1, "An essay develops ideas across structured introduction, body, and conclusion paragraphs.")
        )
    )
}
