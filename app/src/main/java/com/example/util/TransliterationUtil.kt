package com.example.util

import java.util.regex.Pattern

object TransliterationUtil {

    private val SACRED_PHRASES_BN = mapOf(
        "صَلَّى اللَّهُ عَلَيْهِ وَسَلَّمَ" to "সাল্লাল্লাহু ‘আলাইহি ওয়া সাল্লাম",
        "صَلَّى اللهُ عَلَيْهِ وَسَلَّمَ" to "সাল্লাল্লাহু ‘আলাইহি ওয়া সাল্লাম",
        "رَضِيَ اللَّهُ عَنْهُ" to "রাদিয়াল্লাহু ‘আনহু",
        "رَضِيَ اللهُ عَنْهُ" to "রাদিয়াল্লাহু ‘আনহু",
        "رَضِيَ اللَّهُ عَنْهَا" to "রাদিয়াল্লাহু ‘আনহা",
        "رَضِيَ اللهُ عَنْهَا" to "রাদিয়াল্লাহু ‘আনহা",
        "رَضِيَ اللَّهُ عَنْهُمْ" to "রাদিয়াল্লাহু ‘আনহুম",
        "رَضِيَ اللهُ عَنْهُمْ" to "রাদিয়াল্লাহু ‘আনহুম",
        "رَحِمَهُ اللَّهُ" to "রাহিমাহুল্লাহ",
        "رَحِمَهُ اللهُ" to "রাহিমাহুল্লাহ",
        "عَلَيْهِ السَّلَامُ" to "‘আলাইহিস সালাম",
        "عَلَيْهِ السَّلاَمُ" to "‘আলাইহিস সালাম",
        "عَلَيْهِ الصَّلَاةُ وَالسَّلَامُ" to "‘আলাইহিস সালাতু ওয়াস সালাম",
        "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ" to "বিসমিল্লাহির রাহমানির রাহিম",
        "بِسْمِ اللهِ الرَّحْمَنِ الرَّحِيمِ" to "বিসমিল্লাহির রাহমানির রাহিম",
        "أَرَأَيْتُمْ" to "আরাআইতুম",
        "سُبْحَانَ اللَّهِ" to "সুবহানাল্লাহ",
        "سُبْحَانَ اللهِ" to "সুবহানাল্লাহ",
        "الْحَمْدُ لِلَّهِ" to "আলহামদুলিল্লাহ",
        "الْحَمْدُ للهِ" to "আলহামদুলিল্লাহ",
        "لَا إِلَٰهَ إِلَّا اللَّهُ" to "লা ইলাহা ইল্লাল্লাহ",
        "لَا إِلَهَ إِلَّا اللهُ" to "লা ইলাহা ইল্লাল্লাহ",
        "اللَّهُ أَكْبَرُ" to "আল্লাহু আকবার",
        "اللهُ أَكْبَرُ" to "আল্লাহু আকবার",
        "لَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللَّهِ" to "লা হাওলা ওয়ালা কুওয়াতা ইল্লা বিল্লাহ",
        "أَسْتَغْفِرُ اللَّهَ" to "আসতাগফিরুল্লাহ",
        "أَسْتَغْفِرُ اللهَ" to "আসতাগফিরুল্লাহ"
    )

    private val SACRED_PHRASES_EN = mapOf(
        "صَلَّى اللَّهُ عَلَيْهِ وَسَلَّمَ" to "Sallallahu 'Alaihi Wa Sallam",
        "صَلَّى اللهُ عَلَيْهِ وَسَلَّمَ" to "Sallallahu 'Alaihi Wa Sallam",
        "رَضِيَ اللَّهُ عَنْهُ" to "Radhiyallahu 'Anhu",
        "رَضِيَ اللهُ عَنْهُ" to "Radhiyallahu 'Anhu",
        "رَضِيَ اللَّهُ عَنْهَا" to "Radhiyallahu 'Anha",
        "رَضِيَ اللهُ عَنْهَا" to "Radhiyallahu 'Anha",
        "رَضِيَ اللَّهُ عَنْهُمْ" to "Radhiyallahu 'Anhum",
        "رَضِيَ اللهُ عَنْهُمْ" to "Radhiyallahu 'Anhum",
        "رَحِمَهُ اللَّهُ" to "Rahimahullah",
        "رَحِمَهُ اللهُ" to "Rahimahullah",
        "عَلَيْهِ السَّلَامُ" to "'Alaihis Salam",
        "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ" to "Bismillahir Rahmanir Rahim",
        "بِسْمِ اللهِ الرَّحْمَنِ الرَّحِيمِ" to "Bismillahir Rahmanir Rahim",
        "سُبْحَانَ اللَّهِ" to "Subhanallah",
        "الْحَمْدُ لِلَّهِ" to "Alhamdulillah",
        "لَا إِلَٰهَ إِلَّا اللَّهُ" to "La ilaha illallah",
        "اللَّهُ أَكْبَرُ" to "Allahu Akbar"
    )

    private val CONSONANT_MAP_BN = mapOf(
        'ا' to Pair("অ", "অ"), 'أ' to Pair("অ", "অ"), 'إ' to Pair("ই", "ই"), 'آ' to Pair("আ", "আ"),
        'ب' to Pair("ব", "ব"), 'ت' to Pair("ত", "ত"), 'ث' to Pair("ছা", "ছ"), 'ج' to Pair("জ", "জ"),
        'ح' to Pair("হা", "হ"), 'خ' to Pair("খা", "খ"), 'د' to Pair("দ", "দ"), 'ذ' to Pair("যাল", "য"),
        'ر' to Pair("র", "র"), 'ز' to Pair("ঝা", "ঝ"), 'س' to Pair("স", "স"), 'ش' to Pair("শ", "শ"),
        'ص' to Pair("ছাদ", "ছ"), 'ض' to Pair("দাদ", "দ"), 'ط' to Pair("ত্বা", "ত"), 'ظ' to Pair("জ্বা", "জ"),
        'ع' to Pair("‘আ", "‘"), 'غ' to Pair("গাইন", "গ"), 'ف' to Pair("ফা", "ফ"), 'ق' to Pair("ক্বাফ", "ক্ব"),
        'ك' to Pair("কাফ", "ক"), 'ل' to Pair("লাম", "ল"), 'م' to Pair("মীম", "ম"), 'ن' to Pair("নূন", "ন"),
        'ه' to Pair("হা", "হ"), 'و' to Pair("ওয়া", "ওয়"), 'ي' to Pair("য়া", "য়"), 'ى' to Pair("য়া", "য়"),
        'ة' to Pair("তাহ", "ত"), 'ء' to Pair("হামযাহ", "‘")
    )

    private val CONSONANT_MAP_EN = mapOf(
        'ا' to "a", 'أ' to "a", 'إ' to "i", 'آ' to "aa",
        'ب' to "b", 'ت' to "t", 'ث' to "th", 'ج' to "j",
        'ح' to "h", 'خ' to "kh", 'د' to "d", 'ذ' to "dh",
        'ر' to "r", 'ز' to "z", 'س' to "s", 'ش' to "sh",
        'ص' to "s", 'ض' to "d", 'ط' to "t", 'ظ' to "z",
        'ع' to "'", 'غ' to "gh", 'ف' to "f", 'ق' to "q",
        'ك' to "k", 'ل' to "l", 'م' to "m", 'ن' to "n",
        'ه' to "h", 'و' to "w", 'ي' to "y", 'ى' to "y",
        'ة' to "t", 'ء' to "'"
    )

    fun transliterateArabicToBangla(text: String?): String {
        if (text.isNullOrBlank()) return ""
        var processed = text!!
        for ((ar, bn) in SACRED_PHRASES_BN) {
            processed = processed.replace(ar, bn)
        }

        val words = processed.split("\\s+".toRegex())
        val converted = words.map { convertWordBn(it) }
        val out = converted.joinToString(" ")
        return out.replace(Regex("\\s+([,?.!])")) { it.groupValues[1] }
    }

    fun transliterateArabicToEnglish(text: String?): String {
        if (text.isNullOrBlank()) return ""
        var processed = text!!
        for ((ar, en) in SACRED_PHRASES_EN) {
            processed = processed.replace(ar, en)
        }

        val words = processed.split("\\s+".toRegex())
        val converted = words.map { convertWordEn(it) }
        var out = converted.joinToString(" ")
        out = out.replace(Regex("\\s+([,?.!])")) { it.groupValues[1] }
        return if (out.isNotEmpty()) out.substring(0, 1).uppercase() + out.substring(1) else ""
    }

    private fun convertWordBn(w: String): String {
        if (w.isEmpty()) return ""
        if (w.any { it in '\u0980'..'\u09FF' }) return w

        val cleanW = w.replace("[\u064b-\u0652\u0670]".toRegex(), "")
        if (cleanW == "الله" || cleanW == "اللَّه") {
            return when {
                w.endsWith('ِ') -> "আল্লাহি"
                w.endsWith('ُ') -> "আল্লাহু"
                w.endsWith('َ') -> "আল্লাহা"
                else -> "আল্লাহ"
            }
        }
        if (cleanW == "لله" || cleanW == "لِلَّه") {
            return if (w.endsWith('ِ')) "লিল্লাহি" else "লিল্লাহ"
        }
        if (cleanW == "رسول") return "রাসূল"
        if (cleanW == "الرسول" || cleanW == "الرَّسُول") return "আর-রাসূল"
        if (cleanW == "النبي" || cleanW == "النَّبِيّ") return "আন-নাবী"
        if (cleanW == "قال" || cleanW == "قَالَ") return "ক্বালা"
        if (cleanW == "عن" || cleanW == "عَنْ") return "‘আন"

        val res = StringBuilder()
        var i = 0
        val n = w.length

        if ((w.startsWith("ال") || w.startsWith("ٱل")) && n > 2) {
            res.append("আল-")
            i = 2
            if (i < n && w[i] == '\u0652') i++
        }

        while (i < n) {
            val ch = w[i]
            if (ch in "،.؟!:;()[]{}''\"") {
                val punct = when (ch) {
                    '،' -> ","
                    '؟' -> "?"
                    else -> ch.toString()
                }
                res.append(punct)
                i++
                continue
            }
            if (!CONSONANT_MAP_BN.containsKey(ch)) {
                i++
                continue
            }

            val (fullC, rootC) = CONSONANT_MAP_BN[ch]!!
            i++

            var hasShaddah = false
            var harakah: Char? = null

            while (i < n && w[i] in "\u064b\u064c\u064d\u064e\u064f\u0650\u0651\u0652\u0670") {
                if (w[i] == '\u0651') {
                    hasShaddah = true
                } else {
                    harakah = w[i]
                }
                i++
            }

            var longVowel = false
            if (harakah == '\u064e' && i < n && (w[i] == 'ا' || w[i] == 'ى')) {
                longVowel = true
                i++
            } else if (harakah == '\u0650' && i < n && w[i] == 'ي') {
                longVowel = true
                i++
            } else if (harakah == '\u064f' && i < n && w[i] == 'و') {
                longVowel = true
                i++
            }

            var part = when {
                res.isEmpty() && ch in listOf('ا', 'أ', 'إ', 'آ', 'ء', '\u0671') -> {
                    val p = when (harakah) {
                        '\u064e' -> if (longVowel) "আ-" else "আ"
                        '\u0650' -> if (longVowel) "ঈ" else "ই"
                        '\u064f' -> if (longVowel) "ঊ" else "উ"
                        '\u064b' -> "আন"
                        '\u064d' -> "ইন"
                        '\u064c' -> "উন"
                        else -> "আ"
                    }
                    harakah = null
                    p
                }
                res.isEmpty() && harakah == null && !hasShaddah -> fullC
                else -> rootC
            }

            if (hasShaddah) {
                part = when (ch) {
                    'ي' -> "ইয়্য"
                    'و' -> "ওয়্য"
                    else -> part + "্" + rootC
                }
            }

            when (harakah) {
                '\u064e' -> part += if (longVowel) "া-" else "া"
                '\u0650' -> part += if (longVowel) "ী" else "ি"
                '\u064f' -> part += if (longVowel) "ূ" else "ু"
                '\u064b' -> part += "ান"
                '\u064d' -> part += "িন"
                '\u064c' -> part += "ুন"
                '\u0652' -> part += "্"
                '\u0670' -> part += "া-"
            }
            res.append(part)
        }

        var s = res.toString()
        s = s.replace("্+".toRegex(), "্")
        s = s.replace("ওয়্", "ও")
        s = s.replace("অা", "আ")
        s = s.replace("আা", "আ")
        s = s.replace("াা", "া")
        s = s.replace("্ ", " ")
        return s
    }

    private fun convertWordEn(w: String): String {
        if (w.isEmpty()) return ""
        if (w.any { it.code < 128 && it.isLetter() }) return w

        val cleanW = w.replace("[\u064b-\u0652\u0670]".toRegex(), "")
        if (cleanW == "الله" || cleanW == "اللَّه") {
            return when {
                w.endsWith('ِ') -> "Allahi"
                w.endsWith('ُ') -> "Allahu"
                w.endsWith('َ') -> "Allaha"
                else -> "Allah"
            }
        }
        if (cleanW == "لله" || cleanW == "لِلَّه") {
            return if (w.endsWith('ِ')) "lillahi" else "lillah"
        }
        if (cleanW == "رسول") return "Rasul"
        if (cleanW == "الرسول" || cleanW == "الرَّسُول") return "ar-Rasul"
        if (cleanW == "النبي" || cleanW == "النَّبِيّ") return "an-Nabi"
        if (cleanW == "قال" || cleanW == "قَالَ") return "qala"
        if (cleanW == "عن" || cleanW == "عَنْ") return "'an"

        val res = StringBuilder()
        var i = 0
        val n = w.length

        if ((w.startsWith("ال") || w.startsWith("ٱل")) && n > 2) {
            res.append("al-")
            i = 2
            if (i < n && w[i] == '\u0652') i++
        }

        while (i < n) {
            val ch = w[i]
            if (ch in "،.؟!:;()[]{}''\"") {
                val punct = when (ch) {
                    '،' -> ","
                    '؟' -> "?"
                    else -> ch.toString()
                }
                res.append(punct)
                i++
                continue
            }
            if (!CONSONANT_MAP_EN.containsKey(ch)) {
                i++
                continue
            }

            val rootC = CONSONANT_MAP_EN[ch]!!
            i++

            var hasShaddah = false
            var harakah: Char? = null

            while (i < n && w[i] in "\u064b\u064c\u064d\u064e\u064f\u0650\u0651\u0652\u0670") {
                if (w[i] == '\u0651') {
                    hasShaddah = true
                } else {
                    harakah = w[i]
                }
                i++
            }

            var longVowel = false
            if (harakah == '\u064e' && i < n && (w[i] == 'ا' || w[i] == 'ى')) {
                longVowel = true
                i++
            } else if (harakah == '\u0650' && i < n && w[i] == 'ي') {
                longVowel = true
                i++
            } else if (harakah == '\u064f' && i < n && w[i] == 'و') {
                longVowel = true
                i++
            }

            var part = if (hasShaddah) rootC + rootC else rootC

            when (harakah) {
                '\u064e' -> part += if (longVowel) "aa" else "a"
                '\u0650' -> part += if (longVowel) "ee" else "i"
                '\u064f' -> part += if (longVowel) "oo" else "u"
                '\u064b' -> part += "an"
                '\u064d' -> part += "in"
                '\u064c' -> part += "un"
                '\u0670' -> part += "a"
            }
            res.append(part)
        }

        return res.toString()
    }
}
