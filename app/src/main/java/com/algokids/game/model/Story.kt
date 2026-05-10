package com.algokids.game.model

data class Story(
    val id: String,
    val title: String,
    val titleEn: String,
    val content: String,
    val pages: List<String>,
    val pagesEn: List<String>,
    val imageUrl: String? = null,
    val pageImages: List<String>? = null
)
