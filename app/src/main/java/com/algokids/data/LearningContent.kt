package com.algokids.data

data class LearningItem(
    val symbol: String, val titleTr: String, val titleEn: String,
    val detailTr: String, val detailEn: String, val speakTr: String, val speakEn: String
)

object LearningContent {
    private val examplesTr=listOf("arı","balık","ceviz","çiçek","deniz","elma","fındık","güneş","dağ","havuç","ışık","inek","jandarma","kitap","limon","masa","nar","orman","ördek","portakal","roket","saat","şapka","top","uçak","üzüm","vazo","yaprak","zebra")
    val turkishLetters = listOf(
        "A" to "a", "B" to "be", "C" to "ce", "Ç" to "çe", "D" to "de", "E" to "e",
        "F" to "fe", "G" to "ge", "Ğ" to "yumuşak ge", "H" to "he", "I" to "ı", "İ" to "i",
        "J" to "je", "K" to "ke", "L" to "le", "M" to "me", "N" to "ne", "O" to "o",
        "Ö" to "ö", "P" to "pe", "R" to "re", "S" to "se", "Ş" to "şe", "T" to "te",
        "U" to "u", "Ü" to "ü", "V" to "ve", "Y" to "ye", "Z" to "ze"
    )

    fun alphabet(turkish: Boolean): List<LearningItem> {
        val letters = if (turkish) turkishLetters else ('A'..'Z').map { it.toString() to it.toString() }
        return letters.mapIndexed { index,(letter, name) ->
            val lower=letter.lowercase(if(turkish) java.util.Locale.forLanguageTag("tr-TR") else java.util.Locale.US)
            val description=if(turkish) "Harf adı: $name\nBüyük: $letter · Küçük: $lower\n${if(letter in listOf("A","E","I","İ","O","Ö","U","Ü")) "Ünlü harf" else "Ünsüz harf"} · Örnek: ${examplesTr[index]}"+
                if(letter=="Ğ") "\nĞ, sözcük başında kullanılmaz." else "" else "Harf: $letter"
            LearningItem(letter, "$letter harfi", "Letter $letter", description, "Uppercase: $letter · Lowercase: $lower",
                "$name harfi", "Letter $letter")
        } + (if(turkish) listOf("BA", "BE", "BO", "BU", "MA", "ME", "MO", "MU", "LA", "LE", "SA", "SE") else listOf("CAT","DOG","SUN","MAP","HAT","BED","PEN","CUP","BUS","FOX","TOP","RED")).map {
            val lower = it.lowercase(if(turkish) java.util.Locale.forLanguageTag("tr-TR") else java.util.Locale.US)
            LearningItem(it, "$it hecesi", "Word $it", "Birlikte oku: $lower", "Read the word: $lower", lower, lower)
        }
    }

    fun numbers() = (0..100).map {
        LearningItem(it.toString(), "$it sayısı", "Number $it", "Okunuşu: ${numberToTurkish(it)}",
            "Read: ${numberToEnglish(it)}", numberToTurkish(it), numberToEnglish(it))
    }

    fun numberToTurkish(number: Int): String {
        require(number in 0..100)
        if (number == 0) return "sıfır"
        if (number == 100) return "yüz"
        val ones = listOf("", "bir", "iki", "üç", "dört", "beş", "altı", "yedi", "sekiz", "dokuz")
        val tens = listOf("", "on", "yirmi", "otuz", "kırk", "elli", "altmış", "yetmiş", "seksen", "doksan")
        return listOf(tens[number / 10], ones[number % 10]).filter { it.isNotEmpty() }.joinToString(" ")
    }

    fun numberToEnglish(number: Int): String {
        require(number in 0..100)
        if (number == 100) return "one hundred"
        val small = listOf("zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten", "eleven", "twelve", "thirteen", "fourteen", "fifteen", "sixteen", "seventeen", "eighteen", "nineteen")
        val tens = listOf("", "", "twenty", "thirty", "forty", "fifty", "sixty", "seventy", "eighty", "ninety")
        if (number < 20) return small[number]
        return tens[number / 10] + if (number % 10 == 0) "" else "-${small[number % 10]}"
    }
}
