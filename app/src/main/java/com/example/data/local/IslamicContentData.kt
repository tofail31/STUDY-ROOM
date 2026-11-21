package com.example.data.local

import com.example.data.model.CityPreset
import com.example.data.model.DuaItem
import com.example.data.model.DzikirPreset
import com.example.data.model.GuideStep
import com.example.data.model.HijriMilestone

object IslamicContentData {

    val cityPresets = listOf(
        CityPreset("dhaka", "Dhaka", "Bangladesh", 23.8103, 90.4125, 6.0),
        CityPreset("chattogram", "Chattogram", "Bangladesh", 22.3569, 91.7832, 6.0),
        CityPreset("sylhet", "Sylhet", "Bangladesh", 24.8949, 91.8687, 6.0),
        CityPreset("rajshahi", "Rajshahi", "Bangladesh", 24.3745, 88.6042, 6.0),
        CityPreset("khulna", "Khulna", "Bangladesh", 22.8456, 89.5403, 6.0),
        CityPreset("cumilla", "Cumilla", "Bangladesh", 23.4607, 91.1809, 6.0),
        CityPreset("coxsbazar", "Cox's Bazar", "Bangladesh", 21.4272, 92.0058, 6.0),
        CityPreset("makkah", "Makkah", "Saudi Arabia", 21.4225, 39.8262, 3.0),
        CityPreset("madinah", "Madinah", "Saudi Arabia", 24.4672, 39.6112, 3.0),
        CityPreset("kuala_lumpur", "Kuala Lumpur", "Malaysia", 3.1390, 101.6869, 8.0),
        CityPreset("jakarta", "Jakarta", "Indonesia", -6.2088, 106.8456, 7.0)
    )

    val duas = listOf(
        DuaItem(
            id = "dua_pagi_1",
            title = "Morning Du'a for Beneficial Knowledge & Sustenance",
            category = "Morning & Evening",
            arabic = "اللَّهُمَّ إِنِّي أَسْأَلُكَ عِلْمًا نَافِعًا، وَرِزْقًا طَيِّبًا، وَعَمَلًا مُتَقَبَّلًا",
            latin = "Allahumma inni as'aluka 'ilman nafi'an, wa rizqan tayyiban, wa 'amalan mutaqabbalan.",
            translation = "O Allah, I ask You for beneficial knowledge, pure halal sustenance, and deeds that are accepted.",
            reference = "Sunan Ibn Majah 925"
        ),
        DuaItem(
            id = "dua_pagi_2",
            title = "Sayyidul Istighfar (Master Supplication for Forgiveness)",
            category = "Morning & Evening",
            arabic = "اللَّهُمَّ أَنْتَ رَبِّي لَا إِلَهَ إِلَّا أَنْتَ، خَلَقْتَنِي وَأَنَا عَبْدُكَ، وَأَنَا عَلَى عَهْدِكَ وَوَعْدِكَ مَا اسْتَطَعْتُ",
            latin = "Allahumma anta rabbi la ilaha illa anta, khalaqtani wa ana 'abduka, wa ana 'ala 'ahdika wa wa'dika mastata'tu.",
            translation = "O Allah, You are my Lord, none has the right to be worshipped except You. You created me and I am Your servant, and I abide by Your covenant as best I can.",
            reference = "Sahih al-Bukhari 6306"
        ),
        DuaItem(
            id = "dua_harian_1",
            title = "Du'a for Good in Both Worlds",
            category = "Daily",
            arabic = "رَبَّنَا آتِنَا فِي الدُّنْيَا حَسَنَةً وَفِي الْآخِرَةِ حَسَنَةً وَقِنَا عَذَابَ النَّارِ",
            latin = "Rabbana atina fid-dunya hasanatan wa fil-akhirati hasanatan wa qina 'adhaban-nar.",
            translation = "Our Lord, give us in this world that which is good and in the Hereafter that which is good, and protect us from the punishment of the Fire.",
            reference = "Al-Baqarah 2:201"
        ),
        DuaItem(
            id = "dua_harian_2",
            title = "Du'a for Parents",
            category = "Daily",
            arabic = "رَبِّ اغْفِرْ لِي وَلِوَالِدَيَّ وَارْحَمْهُمَا كَمَا رَبَّيَانِي صَغِيرًا",
            latin = "Rabbighfir li wa liwalidayya warhamhuma kama rabbayani saghira.",
            translation = "My Lord, forgive me and my parents, and have mercy upon them as they brought me up when I was small.",
            reference = "Al-Isra 17:24"
        ),
        DuaItem(
            id = "dua_harian_3",
            title = "Du'a Before Sleeping",
            category = "Daily",
            arabic = "بِاسْمِكَ اللَّهُمَّ أَمُوتُ وَأَحْيَا",
            latin = "Bismika Allahumma amutu wa ahya.",
            translation = "In Your name, O Allah, I die and I live.",
            reference = "Sahih al-Bukhari 6324"
        ),
        DuaItem(
            id = "dua_sholat_1",
            title = "Du'a After Adzan",
            category = "Salah",
            arabic = "اللَّهُمَّ رَبَّ هَذِهِ الدَّعْوَةِ التَّامَّةِ وَالصَّلَاةِ الْقَائِمَةِ آتِ مُحَمَّدًا الْوَسِيلَةَ وَالْفَضِيلَةَ",
            latin = "Allahumma rabba hadhihid-da'watit-tammati was-salatil-qa'imati ati Muhammadanil-wasilata wal-fadilah.",
            translation = "O Allah, Lord of this perfect call and established prayer, grant Muhammad the intercession and favor.",
            reference = "Sahih al-Bukhari 614"
        ),
        DuaItem(
            id = "dua_sholat_2",
            title = "Du'a After Dhuha Prayer",
            category = "Salah",
            arabic = "اللَّهُمَّ إِنَّ الضُّحَاءَ ضُحَاؤُكَ، وَالْبَهَاءَ بَهَاؤُكَ، وَالْجَمَالَ جَمَالُكَ، وَالْقُوَّةَ قُوَّتُكَ",
            latin = "Allahumma innad-duha'a duha'uka, wal-baha'a baha'uka, wal-jamala jamaluka, wal-quwwata quwwatuka.",
            translation = "O Allah, indeed the forenoon is Your forenoon, the splendor is Your splendor, the beauty is Your beauty, and the strength is Your strength.",
            reference = "Ma'thur Supplication"
        ),
        DuaItem(
            id = "dua_perjalanan_1",
            title = "Du'a Leaving Home",
            category = "Travel",
            arabic = "بِسْمِ اللَّهِ تَوَكَّلْتُ عَلَى اللَّهِ، لَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللَّهِ",
            latin = "Bismillahi tawakkaltu 'alallah, la hawla wa la quwwata illa billah.",
            translation = "In the name of Allah, I place my trust in Allah; there is no might nor power except with Allah.",
            reference = "Abu Dawud & Tirmidhi"
        ),
        DuaItem(
            id = "dua_perjalanan_2",
            title = "Du'a Mounting Transport",
            category = "Travel",
            arabic = "سُبْحَانَ الَّذِي سَخَّرَ لَنَا هَذَا وَمَا كُنَّا لَهُ مُقْرِنِينَ وَإِنَّا إِلَى رَبِّنَا لَمُنقَلِبُونَ",
            latin = "Subhanalladhi sakhkhara lana hadha wa ma kunna lahu muqrinin wa inna ila rabbina lamunqalibun.",
            translation = "Glory to Him who has subjected this to us, and we could never have it by our efforts. And surely, to our Lord we shall return.",
            reference = "Az-Zukhruf 43:13-14"
        ),
        DuaItem(
            id = "dua_rezeki_1",
            title = "Du'a for Steadfastness of Heart",
            category = "Sustenance & Heart",
            arabic = "يَا مُقَلِّبَ الْقُلُوبِ ثَبِّتْ قَلْبِي عَلَى دِينِكَ",
            latin = "Ya muqallibal-qulubi thabbit qalbi 'ala dinik.",
            translation = "O Turner of the hearts, keep my heart firm upon Your religion.",
            reference = "Jami' at-Tirmidhi 2140"
        ),
        DuaItem(
            id = "dua_rezeki_2",
            title = "Du'a for Ease in Speech & Affairs",
            category = "Sustenance & Heart",
            arabic = "رَبِّ اشْرَحْ لِي صَدْرِي وَيَسِّرْ لِي أَمْرِي وَاحْلُلْ عُقْدَةً مِّن لِّسَانِي",
            latin = "Rabbishrah li sadri wa yassir li amri wahlul 'uqdatam mil-lisani.",
            translation = "My Lord, expand for me my chest, ease for me my task, and untie the knot from my tongue.",
            reference = "Taha 20:25-27"
        )
    )

    val dzikirPresets = listOf(
        DzikirPreset("subhanallah", "Tasbih", "سُبْحَانَ اللَّهِ", "Subhanallah", "Glory be to Allah", 33),
        DzikirPreset("alhamdulillah", "Tahmid", "الْحَمْدُ لِلَّهِ", "Alhamdulillah", "All praise is due to Allah", 33),
        DzikirPreset("allahuakbar", "Takbir", "اللَّهُ أَكْبَرُ", "Allahu Akbar", "Allah is the Greatest", 33),
        DzikirPreset("tahlil", "Tahlil", "لَا إِلَٰهَ إِلَّا اللَّهُ", "La ilaha illallah", "There is no deity worthy of worship except Allah", 33),
        DzikirPreset("istighfar", "Istighfar", "أَسْتَغْفِرُ اللَّهَ الْعَظِيمَ", "Astaghfirullahal-'azim", "I seek forgiveness from Allah the Almighty", 100),
        DzikirPreset("sholawat", "Salawat", "اللَّهُمَّ صَلِّ عَلَى مُحَمَّدٍ", "Allahumma salli 'ala Muhammad", "O Allah, send blessings upon Prophet Muhammad", 100)
    )

    val hijriMilestones = listOf(
        HijriMilestone("1 Muharram", "Islamic New Year", "Marks the Hijrah of Prophet Muhammad (SAW) from Makkah to Madinah."),
        HijriMilestone("10 Muharram", "Day of Ashura", "Recommended to fast on 9th-10th or 10th-11th of Muharram."),
        HijriMilestone("12 Rabi' al-Awwal", "Sirah Remembrance", "Reflecting on the mercy and noble character of Prophet Muhammad (SAW)."),
        HijriMilestone("27 Rajab", "Isra' & Mi'raj", "Night journey and gift of the five daily prayers."),
        HijriMilestone("15 Sha'ban", "Shab-e-Barat / Mid-Sha'ban", "Night of seeking forgiveness and preparing spiritually for Ramadan."),
        HijriMilestone("1 Ramadan", "Start of Ramadan", "Beginning of the blessed month of fasting and Qur'an recitation."),
        HijriMilestone("27 Ramadan", "Laylatul Qadr Nights", "Last ten odd nights of Ramadan—better than a thousand months."),
        HijriMilestone("1 Shawwal", "Eid al-Fitr", "Festival of joy and gratitude after completing Ramadan."),
        HijriMilestone("9 Dhu al-Hijjah", "Day of Arafah", "Recommended fast for non-pilgrims expiating two years of sins."),
        HijriMilestone("10 Dhu al-Hijjah", "Eid al-Adha", "Festival of Sacrifice commemorating Prophet Ibrahim's (AS) devotion.")
    )

    val wudhuSteps = listOf(
        GuideStep(
            1, "Intention & Bismillah",
            "Make a sincere intention in your heart to perform Wudhu for prayer and say Bismillah.",
            "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
            "Bismillahir-Rahmanir-Rahim",
            "In the name of Allah, the Most Gracious, the Most Merciful."
        ),
        GuideStep(2, "Wash Both Hands (3x)", "Wash both hands up to the wrists including between the fingers."),
        GuideStep(3, "Rinse Mouth & Nose (3x)", "Rinse the mouth thoroughly, then sniff water gently into the nostrils and blow it out."),
        GuideStep(4, "Wash Entire Face (3x)", "Wash from the hairline to the chin and from ear to ear."),
        GuideStep(5, "Wash Arms Up to Elbows (3x)", "Wash the right arm including the elbow three times, then the left arm three times."),
        GuideStep(6, "Wipe Head & Ears (1x)", "Wipe wet hands over the head from front to back and back to front, then wipe the ears."),
        GuideStep(7, "Wash Both Feet Up to Ankles (3x)", "Wash the right foot including the ankle and between toes three times, then the left foot."),
        GuideStep(
            8, "Du'a After Wudhu",
            "Recite the Shahadah and supplication after completing Wudhu.",
            "أَشْهَدُ أَنْ لَا إِلَٰهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ وَأَشْهَدُ أَنَّ مُحَمَّدًا عَبْدُهُ وَرَسُولُهُ",
            "Ashhadu alla ilaha illallahu wahdahu la sharika lahu wa ashhadu anna Muhammadan 'abduhu wa rasuluh.",
            "I bear witness that there is no deity worthy of worship except Allah alone, and Muhammad is His servant and messenger."
        )
    )
}
