package com.algokids.game.model

data class GameContent(
    val id: String? = null,
    val type: GameType? = null,
    val instruction: String? = null,
    val questionAssets: List<String>? = null,
    val options: List<String>? = null,
    val answer: String? = null,
    val category: GameCategory? = null,
    val sequence: List<String>? = null
)

enum class GameCategory {
    VISUAL,      // Görsel Algı
    NUMERICAL,   // Sayısal Zeka
    LOGIC,       // Mantık/Algoritma
    ATTENTION,   // Dikkat
    MEMORY,      // Hafıza
    AUDIOLOGY,   // İşitsel Algı
    GEOMETRY,    // Geometri/Şekil
    ALGORITHM    // Algoritmik düşünme
}
