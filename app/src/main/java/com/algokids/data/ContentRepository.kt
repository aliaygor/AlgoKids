package com.algokids.data

import android.content.Context
import com.algokids.game.model.GameCategory
import com.algokids.game.model.GameContent
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ContentRepository(private val context: Context) {
    @Volatile private var cachedContent: List<GameContent>? = null
    @Volatile private var cachedById: Map<String, GameContent>? = null

    fun loadAllContent(): List<GameContent> {
        cachedContent?.let { return it }
        return synchronized(this) {
            cachedContent ?: try {
                val json = context.assets
                    .open("educational_content.json")
                    .bufferedReader()
                    .use { it.readText() }

                val listType = object : TypeToken<List<GameContent>>() {}.type
                Gson().fromJson<List<GameContent>>(json, listType) ?: emptyList()
            } catch (e: Exception) {
                emptyList()
            }.also { cachedContent = it }
        }
    }

    fun getContentByCategory(category: GameCategory): List<GameContent> {
        val byId = cachedById ?: synchronized(this) {
            cachedById ?: loadAllContent()
                .mapNotNull { content -> content.id?.let { id -> id to content } }
                .toMap()
                .also { cachedById = it }
        }
        return Curriculum.foundationIds(category).mapNotNull { byId[it] }
    }

    fun loadPatternContent(): List<GameContent> {
        return getContentByCategory(GameCategory.LOGIC)
    }

}
