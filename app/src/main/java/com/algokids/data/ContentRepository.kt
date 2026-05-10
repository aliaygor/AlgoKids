package com.algokids.data

import android.content.Context
import com.algokids.game.model.GameCategory
import com.algokids.game.model.GameContent
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ContentRepository(private val context: Context) {

    fun loadAllContent(): List<GameContent> {
        return try {
            val json = context.assets
                .open("educational_content.json")
                .bufferedReader()
                .use { it.readText() }

            val listType = object : TypeToken<List<GameContent>>() {}.type
            Gson().fromJson(json, listType)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun getContentByCategory(category: GameCategory): List<GameContent> {
        if (category == GameCategory.ALPHABET) return alphabetContent()
        if (category == GameCategory.NUMBERS) return numberLearningContent()
        return loadAllContent().filter { it.category == category }
    }

    fun loadPatternContent(): List<GameContent> {
        return getContentByCategory(GameCategory.LOGIC)
    }

    private fun alphabetContent() = listOf(
        GameContent("abc1", com.algokids.game.model.GameType.SAME_OBJECT, "A harfi elma ile başlar. A'yı seç.", listOf("A"), listOf("A", "B", "C"), "A", GameCategory.ALPHABET),
        GameContent("abc2", com.algokids.game.model.GameType.SAME_OBJECT, "B harfi balık ile başlar. B'yi seç.", listOf("B"), listOf("D", "B", "E"), "B", GameCategory.ALPHABET),
        GameContent("abc3", com.algokids.game.model.GameType.SAME_OBJECT, "C harfini bul.", listOf("C"), listOf("C", "O", "U"), "C", GameCategory.ALPHABET),
        GameContent("abc4", com.algokids.game.model.GameType.PATTERN, "Harf sırasını tamamla.", sequence = listOf("A", "B", "C"), options = listOf("D", "A"), answer = "D", category = GameCategory.ALPHABET),
        GameContent("abc5", com.algokids.game.model.GameType.WHICH_IS_DIFFERENT, "Farklı harfi bul.", questionAssets = listOf("A", "A", "B", "A"), options = listOf("A", "B"), answer = "B", category = GameCategory.ALPHABET)
    )

    private fun numberLearningContent() = listOf(
        GameContent("numl1", com.algokids.game.model.GameType.SAME_OBJECT, "Bir sayısını bul.", listOf("1"), listOf("1", "2", "3"), "1", GameCategory.NUMBERS),
        GameContent("numl2", com.algokids.game.model.GameType.SAME_OBJECT, "İki sayısını bul.", listOf("2"), listOf("4", "2", "5"), "2", GameCategory.NUMBERS),
        GameContent("numl3", com.algokids.game.model.GameType.COUNTING, "Üç yıldızı say ve 3'ü seç.", listOf("star", "star", "star"), listOf("2", "3", "4"), "3", GameCategory.NUMBERS),
        GameContent("numl4", com.algokids.game.model.GameType.PATTERN, "Sayı sırasını tamamla.", sequence = listOf("1", "2", "3"), options = listOf("4", "2"), answer = "4", category = GameCategory.NUMBERS),
        GameContent("numl5", com.algokids.game.model.GameType.COUNTING, "Beş topu say.", questionAssets = listOf("ball", "ball", "ball", "ball", "ball"), options = listOf("4", "5", "6"), answer = "5", category = GameCategory.NUMBERS)
    )
}
