package com.algokids.game.model

data class Story(
    val id: String,
    val title: String,
    val content: String,
    val pages: List<String>,
    val imageUrl: String? = null,
    val pageImages: List<String>? = null
)
