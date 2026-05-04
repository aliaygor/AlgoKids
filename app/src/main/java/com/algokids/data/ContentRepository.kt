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
        return loadAllContent().filter { it.category == category }
    }

    fun loadPatternContent(): List<GameContent> {
        return getContentByCategory(GameCategory.LOGIC)
    }
}
