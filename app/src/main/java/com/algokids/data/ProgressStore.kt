package com.algokids.data

import android.content.Context
import com.algokids.game.model.GameCategory
import com.algokids.ui.screens.GameSessionState
import com.algokids.ui.screens.QuestionProgress
import com.google.gson.Gson

/** Only plain values are serialized; Compose delegates never enter the saved data. */
class ProgressStore(context: Context) {
    private val preferences = context.getSharedPreferences("algokids_progress", Context.MODE_PRIVATE)
    private val gson = Gson()
    private data class SavedSession(val index: Int = 0, val correct: Int = 0, val mistakes: Int = 0, val questions: Map<String, QuestionProgress> = emptyMap())

    fun load(category: GameCategory): GameSessionState {
        val state = GameSessionState()
        val saved = runCatching { gson.fromJson(preferences.getString("session_v${Curriculum.REVISION}_${category.name}", null), SavedSession::class.java) }.getOrNull()
        if(saved != null) {
            state.index = saved.index.coerceAtLeast(0)
            state.correctCount = saved.correct.coerceAtLeast(0)
            state.mistakeCount = saved.mistakes.coerceAtLeast(0)
            state.questions.putAll(saved.questions)
        }
        return state
    }

    fun save(category: GameCategory, state: GameSessionState) {
        preferences.edit().putString("session_v${Curriculum.REVISION}_${category.name}", gson.toJson(SavedSession(state.index,state.correctCount,state.mistakeCount,state.questions.toMap()))).apply()
    }

    fun foundationCompleted(category:GameCategory):Boolean {
        val ids=Curriculum.foundationIds(category)
        val state=load(category)
        return ids.isNotEmpty() && ids.all { state.questions[it]?.isCorrect==true }
    }

    /** Clear achievements in both languages, retaining sound, age, ads and notification settings. */
    fun resetLearning() {
        val edit=preferences.edit()
        preferences.all.keys.filter { key ->
            key.startsWith("challenge_") || key.startsWith("session_") || key.startsWith("route_") ||
                key.startsWith("learning_") || key.startsWith("daily_") || key=="bonus_points" || key=="recent_category"
        }.forEach { edit.remove(it) }
        edit.commit()
    }
}
