package com.algokids

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.algokids.game.model.GameCategory
import com.algokids.game.model.Story
import com.algokids.ui.screens.AlgorithmGameScreen
import com.algokids.ui.screens.AppLanguage
import com.algokids.ui.screens.GameScreen
import com.algokids.ui.screens.GameSessionState
import com.algokids.ui.screens.HomeScreen
import com.algokids.ui.screens.LearningScreen
import com.algokids.ui.screens.StoryScreen
import com.algokids.ui.theme.AlgoKidsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AlgoKidsTheme {
                val repo = remember { com.algokids.data.ContentRepository(this) }
                var selectedCategory by remember { mutableStateOf<GameCategory?>(null) }
                var selectedStory by remember { mutableStateOf<Story?>(null) }
                var language by rememberSaveable { mutableStateOf(AppLanguage.TR) }
                var isSoundEnabled by rememberSaveable { mutableStateOf(true) }
                val gameSessions = remember { mutableStateMapOf<GameCategory, GameSessionState>() }

                when {
                    selectedStory != null -> {
                        StoryScreen(
                            story = selectedStory!!,
                            language = language,
                            isSoundEnabled = isSoundEnabled,
                            onToggleSound = { isSoundEnabled = !isSoundEnabled },
                            onBack = { selectedStory = null }
                        )
                    }
                    selectedCategory != null -> {
                        val category = selectedCategory!!
                        if (category == GameCategory.ALGORITHM) {
                            AlgorithmGameScreen(
                                language = language,
                                isSoundEnabled = isSoundEnabled,
                                onToggleSound = { isSoundEnabled = !isSoundEnabled },
                                onBack = { selectedCategory = null }
                            )
                        } else if (category == GameCategory.ALPHABET || category == GameCategory.NUMBERS) {
                            LearningScreen(
                                category = category,
                                language = language,
                                isSoundEnabled = isSoundEnabled,
                                onBack = { selectedCategory = null }
                            )
                        } else {
                            val list = remember(selectedCategory) {
                                repo.getContentByCategory(category)
                            }
                            GameScreen(
                                items = list,
                                session = gameSessions.getOrPut(category) { GameSessionState() },
                                language = language,
                                isSoundEnabled = isSoundEnabled,
                                onToggleSound = { isSoundEnabled = !isSoundEnabled },
                                onBack = { selectedCategory = null }
                            )
                        }
                    }
                    else -> {
                        HomeScreen(
                            language = language,
                            onLanguageChange = { language = it },
                            onCategorySelect = { selectedCategory = it },
                            onStorySelect = { selectedStory = it },
                            performanceSummary = gameSessions.map { (category, session) ->
                                val name = when (category) {
                                    GameCategory.VISUAL -> if (language == AppLanguage.TR) "Görsel Algı" else "Visual"
                                    GameCategory.NUMERICAL -> if (language == AppLanguage.TR) "Sayısal Mantık" else "Numbers"
                                    GameCategory.LOGIC -> if (language == AppLanguage.TR) "Mantık Yürütme" else "Logic"
                                    GameCategory.ATTENTION -> if (language == AppLanguage.TR) "Dikkat" else "Attention"
                                    GameCategory.MEMORY -> if (language == AppLanguage.TR) "Hafıza" else "Memory"
                                    GameCategory.AUDIOLOGY -> if (language == AppLanguage.TR) "İşitsel" else "Listening"
                                    GameCategory.GEOMETRY -> if (language == AppLanguage.TR) "Geometri" else "Geometry"
                                    GameCategory.ALPHABET -> if (language == AppLanguage.TR) "Alfabe" else "Alphabet"
                                    GameCategory.NUMBERS -> if (language == AppLanguage.TR) "Sayılar" else "Numbers"
                                    GameCategory.ALGORITHM -> if (language == AppLanguage.TR) "Algoritma" else "Algorithm"
                                }
                                "$name: ${if (language == AppLanguage.TR) "Doğru" else "Right"} ${session.correctCount}, ${if (language == AppLanguage.TR) "Hata" else "Miss"} ${session.mistakeCount}"
                            }
                        )
                    }
                }
            }
        }
    }
}
