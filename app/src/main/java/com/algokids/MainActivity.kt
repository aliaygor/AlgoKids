package com.algokids

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.algokids.game.model.GameCategory
import com.algokids.game.model.Story
import com.algokids.ui.screens.GameScreen
import com.algokids.ui.screens.HomeScreen
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

                when {
                    selectedStory != null -> {
                        StoryScreen(
                            story = selectedStory!!,
                            onBack = { selectedStory = null }
                        )
                    }
                    selectedCategory != null -> {
                        val list = remember(selectedCategory) { 
                            repo.getContentByCategory(selectedCategory!!) 
                        }
                        GameScreen(
                            items = list,
                            onBack = { selectedCategory = null }
                        )
                    }
                    else -> {
                        HomeScreen(
                            onCategorySelect = { selectedCategory = it },
                            onStorySelect = { selectedStory = it }
                        )
                    }
                }
            }
        }
    }
}
