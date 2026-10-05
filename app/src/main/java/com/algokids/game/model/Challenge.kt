package com.algokids.game.model

import com.algokids.game.engine.GridRules

enum class ChallengeMode { ORDER, CHOICE, MULTI, RECALL, LISTEN, LISTEN_CHOICE, GRID, GRID_RECALL, SCAN }

/** Each workshop owns a distinct problem-solving process. Rendering widgets may be shared. */
enum class LearningMechanic(val owner: GameCategory) {
    LOOP_COMPRESSION(GameCategory.ALGORITHM), FIRST_BUG(GameCategory.ALGORITHM), BRANCH_EXECUTION(GameCategory.ALGORITHM),
    OPERATION_PIPELINE(GameCategory.NUMERICAL), BALANCE_UNKNOWN(GameCategory.NUMERICAL),
    DEPENDENCY_PLANNING(GameCategory.LOGIC), CLUE_ELIMINATION(GameCategory.LOGIC),
    RESPONSE_INHIBITION(GameCategory.ATTENTION), RULE_SWITCHING(GameCategory.ATTENTION),
    LOCATION_RECALL(GameCategory.MEMORY), REVERSE_WORKING_MEMORY(GameCategory.MEMORY),
    SPOKEN_INSTRUCTIONS(GameCategory.AUDIOLOGY), SPOKEN_INFERENCE(GameCategory.AUDIOLOGY),
    MENTAL_ROTATION(GameCategory.GEOMETRY), AXIS_REFLECTION(GameCategory.GEOMETRY),
    LAYER_INTEGRATION(GameCategory.VISUAL), MISSING_MOSAIC(GameCategory.VISUAL),
    ALPHABET_ORDER(GameCategory.ALPHABET), SYLLABLE_SYNTHESIS(GameCategory.ALPHABET),
    NUMBER_NAMING(GameCategory.NUMBERS), PLACE_VALUE(GameCategory.NUMBERS)
}

data class BeforeRule(val first: String, val second: String)

data class GridTask(val size: Int, val panels: List<Set<Int>>, val initial: Set<Int> = emptySet())

data class ChallengeRound(
    val promptTr: String, val promptEn: String,
    val options: List<String>, val answer: List<String>,
    val explanationTr: String, val explanationEn: String,
    val stimulus: List<String> = emptyList(),
    val grid: GridTask? = null,
    val dependencies: List<BeforeRule> = emptyList(),
    val spokenTr: String = "", val spokenEn: String = "",
    val stageRulesTr: List<String> = emptyList(), val stageRulesEn: List<String> = emptyList(),
    val labelsTr: Map<String, String> = emptyMap(), val labelsEn: Map<String, String> = emptyMap()
) {
    fun accepts(response: List<String>, mode: ChallengeMode): Boolean {
        if (dependencies.isNotEmpty()) return response.size == options.size && response.toSet() == options.toSet() &&
            dependencies.all { response.indexOf(it.first) < response.indexOf(it.second) }
        return if (mode in setOf(ChallengeMode.MULTI, ChallengeMode.GRID, ChallengeMode.GRID_RECALL))
            response.size == answer.size && response.toSet() == answer.toSet()
        else response == answer
    }
}

data class Challenge(
    val id: String, val category: GameCategory, val titleTr: String, val titleEn: String,
    val skillTr: String, val skillEn: String, val mode: ChallengeMode,
    val rounds: List<ChallengeRound>, val mechanic: LearningMechanic
)

object ChallengeCatalog {
    private fun r(tr: String, en: String, options: List<String>, answer: List<String>, whyTr: String, whyEn: String,
                  stimulus: List<String> = emptyList()) = ChallengeRound(tr, en, options, answer, whyTr, whyEn, stimulus)
    private fun gridRound(tr: String, en: String, panels: List<Set<Int>>, answer: Set<Int>, whyTr: String, whyEn: String,
                          initial: Set<Int> = emptySet(), size: Int = 3) =
        r(tr, en, (0 until size * size).map(Int::toString), answer.sorted().map(Int::toString), whyTr, whyEn)
            .copy(grid = GridTask(size, panels, initial))

    val all: List<Challenge> = buildList {
        fun addWorkshop(id: String, titleTr: String, titleEn: String, skillTr: String, skillEn: String,
                        mode: ChallengeMode, mechanic: LearningMechanic, rounds: List<ChallengeRound>) {
            add(Challenge(id, mechanic.owner, titleTr, titleEn, skillTr, skillEn, mode, rounds, mechanic))
        }
        addWorkshop("loops", "Döngü laboratuvarı", "Loop laboratory", "Komutları tekrar bloklarına dönüştürme", "Compress repeated commands",
            ChallengeMode.ORDER, LearningMechanic.LOOP_COMPRESSION, listOf(
                r("→ → ↑ ↑ yolunu iki döngüyle kur.", "Build → → ↑ ↑ using two loops.", listOf("→ × 2", "↑ × 2", "→ × 1", "↓ × 2"), listOf("→ × 2", "↑ × 2"), "Önce iki sağ, sonra iki yukarı: iki blok dört hareket üretir.", "Two right moves, then two up moves: two blocks produce four moves."),
                r("→ ↑ → ↑ → ↑ yolundaki TEKRAR EDEN gövdeyi tek blokla seç.", "Choose one block for the REPEATING body in → ↑ → ↑ → ↑.", listOf("(→ ↑) × 3", "→ × 3", "(↑ →) × 3", "(→ ↑) × 2"), listOf("(→ ↑) × 3"), "Tekrar eden parça yalnızca bir yön değil, → ↑ ikilisidir. Üç tekrar altı hareket üretir.", "The repeated body is the pair → ↑. Three repeats produce six moves."),
                r("↑ → → ↑ → → ↓ yolunu bir döngü ve son komutla kur.", "Build ↑ → → ↑ → → ↓ using one loop and one final command.", listOf("(↑ → →) × 2", "↓", "(→ ↑) × 2", "↑ × 2"), listOf("(↑ → →) × 2", "↓"), "Üç komutlu gövde iki kez çalışır; döngü bittikten sonra ↓ gelir. Toplam yedi hareket.", "The three-command body runs twice. Then ↓ runs outside the loop: seven moves.")
            ))
        addWorkshop("debug", "Hata dedektifi", "Bug detective", "İlk hatayı izleyerek bulma", "Trace the first bug",
            ChallengeMode.CHOICE, LearningMechanic.FIRST_BUG, listOf(
                r("Hedef program → → ↑ olmalı. Görünen programın ilk hatalı komutu hangisi?", "The target program is → → ↑. Which is the first incorrect command below?", listOf("1", "2", "3"), listOf("2"), "İlk ok doğru. İkinci ok sağ yerine yukarı gidiyor. Sonraki hataları incelemeden ilkini düzelt.", "The first arrow is correct. The second should be right. Fix the first bug before later ones.", listOf("→", "↑", "↑")),
                r("(→ ↑) × 2 açılınca dört komut oluşmalı. İlk bozuk komut nerede?", "Expanding (→ ↑) × 2 should produce four commands. Where is the first bug?", listOf("1", "2", "3", "4"), listOf("3"), "Beklenen sıra → ↑ → ↑. İlk iki adım doğru; üçüncü adım sola gitmemeli.", "Expected: → ↑ → ↑. The first two are correct; the third must not go left.", listOf("→", "↑", "←", "↑")),
                r("Engel varsa ↑, yoksa →. Durumlar: boş, engel, boş, engel. İlk yanlış komut nerede?", "If blocked use ↑, otherwise →. States: clear, blocked, clear, blocked. Where is the first bug?", listOf("1", "2", "3", "4"), listOf("4"), "Her adımı kendi durumuna göre denetle. Dördüncü durumda engel var; → yerine ↑ gerekir.", "Check each step against its state. Step four is blocked, so it needs ↑ instead of →.", listOf("→", "↑", "→", "→"))
            ))
        addWorkshop("conditions", "Robotun kararları", "Robot decisions", "Duruma göre farklı komut çalıştırma", "Execute a branch for each state",
            ChallengeMode.ORDER, LearningMechanic.BRANCH_EXECUTION, listOf(
                r("Robot her karede karar versin: engel 🧱 varsa ↑, boş ○ ise →. Durumların komutlarını sırayla kur.", "At each square: use ↑ for a wall 🧱, or → for clear ○. Build commands for the states in order.", listOf("↑", "→", "↓"), listOf("→", "↑", "→"), "Her durum için ayrı karar: boş →, engel ↑, boş →.", "Make a fresh decision for each state: clear →, wall ↑, clear →.", listOf("○", "🧱", "○")),
                r("Pil az 🔴 ise şarj 🔋, dolu 🟢 ise ilerle →. Her pil durumuna karşılık gelen komutu kur.", "For low battery 🔴 charge 🔋; for full 🟢 move →. Build a command for each battery state.", listOf("🔋", "→", "↑"), listOf("🔋", "→", "🔋", "→"), "Komut sıraya göre değil, pil durumuna göre değişir.", "The action depends on the battery state, not on a memorized alternating rule.", listOf("🔴", "🟢", "🔴", "🟢")),
                r("Anahtar 🔑 varsa kapıyı aç 🚪. Anahtar yokken pil az 🔴 ise şarj 🔋, değilse bekle ⏸. Komutları kur.", "If a key 🔑 is present open 🚪. Without a key, charge 🔋 if battery is low 🔴, otherwise wait ⏸. Build the commands.", listOf("🚪", "🔋", "⏸", "→"), listOf("🚪", "🔋", "⏸", "🚪"), "Önce anahtarı kontrol et. Anahtar varsa pil koşuluna geçmeden kapıyı aç.", "Check the key first. A key opens the door before the battery condition is considered.", listOf("🔑🔴", "○🔴", "○🟢", "🔑🟢"))
            ))
        addWorkshop("number_machine", "Sayı makinesi", "Number machine", "Değişen işlem zincirlerini uygulama", "Apply different operation pipelines",
            ChallengeMode.CHOICE, LearningMechanic.OPERATION_PIPELINE, listOf(
                r("Girdi 3. Önce 2 ekle, sonra 2 ile çarp. Sonuç?", "Input 3. Add 2, then multiply by 2. Result?", listOf("10", "8", "7", "12"), listOf("10"), "3 + 2 = 5; 5 × 2 = 10. Sırayı değiştirmek başka sonuç verir.", "3 + 2 = 5; 5 × 2 = 10. Reversing the order changes the result."),
                r("Girdi 8. Önce 2'ye böl, sonra 3 ekle. Sonuç?", "Input 8. Divide by 2, then add 3. Result?", listOf("7", "6", "11", "4"), listOf("7"), "8 ÷ 2 = 4; 4 + 3 = 7.", "8 ÷ 2 = 4; 4 + 3 = 7."),
                r("Girdi 5. Önce 3 ekle, sonra 2 ile çarp, en son 4 çıkar.", "Input 5. Add 3, multiply by 2, then subtract 4.", listOf("12", "16", "9", "4"), listOf("12"), "5 → 8 → 16 → 12. Her ara sonucu sonraki makineye ver.", "5 → 8 → 16 → 12. Pass each intermediate result to the next machine.")
            ))
        addWorkshop("number_order", "Terazi dengesi", "Balance puzzle", "Eşitliği bozmadan bilinmeyeni bulma", "Find an unknown while preserving equality",
            ChallengeMode.CHOICE, LearningMechanic.BALANCE_UNKNOWN, listOf(
                r("Terazide □ + 3 = 8. Kutudaki sayı kaç?", "The scale shows □ + 3 = 8. What is inside the box?", listOf("5", "11", "3", "8"), listOf("5"), "İki taraftan da 3 çıkarırsan eşitlik korunur: □ = 5.", "Subtract 3 from both sides to keep the balance: □ = 5."),
                r("İki eşit kutu toplam 12 ediyor: □ + □ = 12. Bir kutu kaç?", "Two identical boxes total 12: □ + □ = 12. What is in one box?", listOf("6", "12", "10", "4"), listOf("6"), "Eşit iki parçaya ayır: 12 ÷ 2 = 6.", "Split the total into two equal parts: 12 ÷ 2 = 6."),
                r("İki eşit kutu ve 2 ağırlık toplam 16: □ + □ + 2 = 16. Bir kutu kaç?", "Two identical boxes and 2 total 16: □ + □ + 2 = 16. What is in one box?", listOf("7", "8", "14", "6"), listOf("7"), "Önce 2'yi iki taraftan çıkar: 14. Sonra iki eşit kutuya böl: 7.", "Remove 2 from both sides to get 14. Divide between two equal boxes: 7.")
            ))
        val planLabelsTr = mapOf("🪴" to "Saksı", "🌰" to "Tohum", "💧" to "Sula", "☀️" to "Işık", "🌱" to "Filiz", "🪨" to "Temel", "🧱" to "Sütun", "🌉" to "Yol", "🚗" to "Araç")
        val planLabelsEn = mapOf("🪴" to "Pot", "🌰" to "Seed", "💧" to "Water", "☀️" to "Light", "🌱" to "Sprout", "🪨" to "Foundation", "🧱" to "Pillar", "🌉" to "Road", "🚗" to "Car")
        addWorkshop("dependencies", "Planlama masası", "Planning table", "Bağımlılıkları gözeten geçerli plan kurma", "Build valid plans from dependencies",
            ChallengeMode.ORDER, LearningMechanic.DEPENDENCY_PLANNING, listOf(
                r("Tohum saksıdan sonra; sulama tohumdan sonra olmalı. Kartları geçerli bir plana dönüştür.", "Seed comes after the pot; water comes after the seed. Build a valid plan.", listOf("💧", "🪴", "🌰"), listOf("🪴", "🌰", "💧"), "Hazır olmayan bir adıma başlayamazsın: saksı → tohum → sulama.", "A step must wait for its preparation: pot → seed → water.").copy(dependencies = listOf(BeforeRule("🪴", "🌰"), BeforeRule("🌰", "💧"))),
                r("Filiz için hem sulama hem ışık hazır olmalı. Sulama tohumdan, tohum saksıdan sonra. Işığı uygun herhangi bir sırada yerleştirebilirsin.", "A sprout needs water and light. Water follows the seed; the seed follows the pot. Light can be prepared at any valid point.", listOf("🌱", "💧", "☀️", "🌰", "🪴"), listOf("🪴", "🌰", "💧", "☀️", "🌱"), "Bağımsız işleri farklı sıraya koyabilirsin. Önemli olan filizden önce su ve ışığın hazır olması.", "Independent tasks may be ordered differently. Water and light must both be ready before the sprout.").copy(dependencies = listOf(BeforeRule("🪴", "🌰"), BeforeRule("🌰", "💧"), BeforeRule("💧", "🌱"), BeforeRule("☀️", "🌱"))),
                r("Araç yoldan sonra geçer. Yol sütunlardan; sütunlar temelden sonra yapılır. İpuçlarını bir plana çevir.", "The car crosses after the road is ready. The road needs pillars; pillars need the foundation. Turn these clues into a plan.", listOf("🌉", "🚗", "🧱", "🪨"), listOf("🪨", "🧱", "🌉", "🚗"), "Sonuçtan geriye düşün: araç için yol, yol için sütun, sütun için temel gerekir.", "Work backwards: a car needs a road, a road needs pillars, and pillars need a foundation.").copy(dependencies = listOf(BeforeRule("🪨", "🧱"), BeforeRule("🧱", "🌉"), BeforeRule("🌉", "🚗")))
            ).map { it.copy(labelsTr = planLabelsTr, labelsEn = planLabelsEn) })
        addWorkshop("reverse_rule", "İpucu dedektifi", "Clue detective", "Olasılıkları eleyerek çıkarım yapma", "Eliminate possibilities using clues",
            ChallengeMode.CHOICE, LearningMechanic.CLUE_ELIMINATION, listOf(
                r("Kedi 🐱, köpek 🐶 ve kuş 🐦 üç farklı evde. Kırmızı evde kedi var. Mavi evde köpek yok. Mavi evde kim var?", "Cat 🐱, dog 🐶 and bird 🐦 occupy different homes. Red has the cat. Blue does not have the dog. Who is in blue?", listOf("🐱", "🐶", "🐦"), listOf("🐦"), "Mavi ev kedi olamaz: kedi kırmızıda. Köpek de olamaz. Geriye kuş kalır.", "Blue cannot have the cat, which is in red, or the dog. Only the bird remains."),
                r("Ada, Bora ve Cem farklı meyveler aldı: elma 🍎, muz 🍌, üzüm 🍇. Ada muz aldı; Cem elma almadı. Bora ne aldı?", "Ada, Bora and Cem each have a different fruit: apple 🍎, banana 🍌, grapes 🍇. Ada has banana; Cem does not have apple. What does Bora have?", listOf("🍎", "🍌", "🍇"), listOf("🍎"), "Ada muzda. Cem muz alamaz; elma da almadığı için üzüm aldı. Elma Bora'ya kalır.", "Ada has banana. Cem cannot have banana or apple, so has grapes. Bora has apple."),
                r("Kedi, köpek, kuş yan yana. Kuş en sağda; kedi köpeğin hemen solunda. Soldan sağa sıra ne?", "Cat, dog and bird stand in a row. The bird is rightmost; the cat is immediately left of the dog. What is the left-to-right order?", listOf("🐱🐶🐦", "🐦🐱🐶", "🐶🐱🐦", "🐱🐦🐶"), listOf("🐱🐶🐦"), "Kuş en sağda olduğu için kedi ve köpek soldaki iki yere gelir. Sıra kedi → köpek → kuş.", "The bird is rightmost, leaving the left two places for cat then dog.")
            ))
        val scanCards = listOf(listOf("🔵▲", "🔴▲", "🔵●", "🔵▲", "🔴●", "🔵▲"), listOf("🔴●", "🔵●", "🔴▲", "🔴●", "🔵▲", "🔴▲", "🔴●"), listOf("🔵●", "🔴▲", "🔵▲", "🔴●", "🔵▲", "🔵●", "🔴▲", "🔵▲"))
        addWorkshop("attention_filter", "Dur ve seç", "Stop and select", "Hedefte tepki verip yanıltıcı kartta durma", "Respond to targets and inhibit distractors",
            ChallengeMode.SCAN, LearningMechanic.RESPONSE_INHIBITION, scanCards.mapIndexed { index, cards ->
                val target = if (index == 1) "🔴●" else "🔵▲"
                r("Yalnızca $target kartında SEÇ; diğer kartlarda GEÇ. Acele etmene gerek yok.", "SELECT only $target; SKIP the other cards. There is no need to rush.", listOf("✓", "–"), cards.map { if (it == target) "✓" else "–" }, "Renk ve şekli birlikte kontrol et. Hedefe benzeyen kartlarda tepkiyi durdur.", "Check color and shape together. Hold back on cards that only resemble the target.", cards)
                    .copy(stageRulesTr = List(cards.size) { "Hedef: $target" }, stageRulesEn = List(cards.size) { "Target: $target" })
            })
        addWorkshop("attention_switch", "Kural değişti!", "Rule switch!", "Bir akışın içinde yeni kurala geçme", "Switch rules within a stream",
            ChallengeMode.SCAN, LearningMechanic.RULE_SWITCHING, scanCards.mapIndexed { index, cards ->
                val targets = cards.indices.map { if (it < 2 + index) "🔵▲" else "🔴●" }
                r("Kartlar ilerlerken ekrandaki hedef değişecek. Her kartı O ANDAKİ kurala göre seç veya geç.", "The target changes as cards advance. Select or skip each card using the CURRENT rule.", listOf("✓", "–"), cards.mapIndexed { i, card -> if (card == targets[i]) "✓" else "–" }, "Eski hedefi sürdürmek yerine her adımda yeni hedefi kontrol et.", "Check the current target at every step instead of carrying on with the old rule.", cards)
                    .copy(stageRulesTr = targets.map { "Şimdiki hedef: $it" }, stageRulesEn = targets.map { "Current target: $it" })
            })
        val locations = listOf(setOf(0, 4, 8), setOf(1, 3, 5, 7), setOf(0, 2, 4, 6, 8))
        addWorkshop("memory_order", "Yer hafızası", "Location memory", "Gizlenen nesnelerin konumlarını hatırlama", "Recall hidden locations",
            ChallengeMode.GRID_RECALL, LearningMechanic.LOCATION_RECALL, locations.map {
                gridRound("Işıklı karelerin YERİNİ incele. Hazır olduğunda gizle ve aynı yerleri işaretle.", "Study the POSITIONS of the lit squares. Hide them when ready and mark the same locations.", listOf(it), it, "Nesne adını değil, konumları hatırladın. Köşeler ve orta kareyi dayanak olarak kullanabilirsin.", "You recalled locations rather than object names. Use corners and the center as anchors.")
            })
        val sequences = listOf(listOf("🌙", "⭐", "☀️"), listOf("⭐", "🌙", "⭐", "☁️"), listOf("☀️", "☁️", "🌙", "☀️", "⭐"))
        addWorkshop("memory_reverse", "Tersine hatırla", "Remember backwards", "Gizlenen bilgiyi akılda dönüştürme", "Transform hidden information in memory",
            ChallengeMode.RECALL, LearningMechanic.REVERSE_WORKING_MEMORY, sequences.map { sequence ->
                r("Sırayı incele; gizledikten sonra SONDAN BAŞA kur.", "Study the sequence; after hiding it rebuild from LAST to FIRST.", listOf("🌙", "⭐", "☀️", "☁️"), sequence.reversed(), "Son öğeden başlayıp geri gittin. Tekrarlanan sembolleri atlamadan say.", "Work back from the last item. Keep repeated symbols rather than dropping them.", sequence)
            })
        val spokenCommands = listOf(
            Triple("Önce yıldız, sonra ay.", "First star, then moon.", listOf("⭐", "🌙")),
            Triple("Ay ile başla, güneş ile bitir. Araya yıldız koy.", "Start with the moon and end with the sun. Put the star between them.", listOf("🌙", "⭐", "☀️")),
            Triple("Güneşe dokun. Sonra aya iki kez dokun. En son yıldız.", "Tap the sun, then tap the moon twice, then the star.", listOf("☀️", "🌙", "🌙", "⭐"))
        )
        addWorkshop("listening_order", "Komutu dinle", "Listen to instructions", "Konuşulan ilişkileri eylem sırasına çevirme", "Turn spoken instructions into actions",
            ChallengeMode.LISTEN, LearningMechanic.SPOKEN_INSTRUCTIONS, spokenCommands.map { (tr, en, answer) ->
                r("Yönergeyi dinle ve kartlara istenen sırayla dokun.", "Listen to the instruction and tap the cards in the requested order.", listOf("⭐", "🌙", "☀️", "☁️"), answer, "Duyduğun başlangıç, aradaki öğe ve tekrar sayısını eyleme çevirdin.", "You converted spoken starts, positions and repeat counts into actions.").copy(spokenTr = tr, spokenEn = en)
            })
        val spokenClues = listOf(
            Triple("Gece gökyüzünde görünürüm. Bazen hilal olurum. Ben kimim?", "I appear in the night sky. Sometimes I am a crescent. What am I?", "🌙"),
            Triple("Yağmurdan sonra güneş açınca gökyüzünde birçok renk olarak görünürüm. Ben kimim?", "After rain, when the sun comes out, I appear as many colors in the sky. What am I?", "🌈"),
            Triple("Gökyüzünde bulunurum; yağmur damlalarını taşırım. Bazen güneşi örterim. Ben kimim?", "I am in the sky and carry raindrops. Sometimes I cover the sun. What am I?", "☁️")
        )
        addWorkshop("listening_reverse", "Duyduğundan anlam çıkar", "Infer from speech", "Sesli ipuçlarının ortak anlamını bulma", "Combine the meaning of spoken clues",
            ChallengeMode.LISTEN_CHOICE, LearningMechanic.SPOKEN_INFERENCE, spokenClues.map { (tr, en, answer) ->
                r("İpuçlarını dinle. Bütün ipuçlarına uyan resmi seç.", "Listen to the clues. Choose the picture that fits all of them.", listOf("🌙", "☀️", "☁️", "🌈"), listOf(answer), "Tek bir sözcüğe değil, bütün ipuçlarının ortak anlamına baktın.", "Use all the clues together instead of reacting to a single word.").copy(spokenTr = tr, spokenEn = en)
            })
        val shapes = listOf(setOf(0, 3, 6, 7), setOf(0, 1, 4), setOf(1, 3, 4, 5))
        addWorkshop("geometry_turn", "Şekli zihninde döndür", "Rotate a shape mentally", "Bir şeklin dönüşten sonraki konumunu kurma", "Construct the rotated shape",
            ChallengeMode.GRID, LearningMechanic.MENTAL_ROTATION, shapes.mapIndexed { index, shape ->
                val turns = index + 1
                gridRound("Şekli saat yönünde $turns çeyrek tur döndür. Son halinin karelerini işaretle.", "Rotate the shape clockwise $turns quarter turns. Mark its resulting squares.", listOf(shape), GridRules.rotate(shape, 3, turns), "Her çeyrek turda satır ve sütun yer değiştirir. Şeklin parça sayısı korunur.", "Each quarter turn changes row and column positions. The number of filled squares stays the same.")
            })
        addWorkshop("geometry_sides", "Katla ve aç", "Fold and unfold", "Dikey ve yatay eksene göre simetri", "Reflect across vertical and horizontal axes",
            ChallengeMode.GRID, LearningMechanic.AXIS_REFLECTION, shapes.mapIndexed { index, shape ->
                val vertical = index != 1
                gridRound("${if (vertical) "Dikey" else "Yatay"} orta çizgi ayna olsun. Şeklin karşı taraftaki yansımasını işaretle.", "Use the ${if (vertical) "vertical" else "horizontal"} center line as a mirror. Mark the reflected shape.", listOf(shape), GridRules.reflect(shape, 3, vertical), "Aynaya uzaklık değişmez. ${if (vertical) "Sağ ve sol" else "Üst ve alt"} yer değiştirir.", "Distance from the mirror stays the same. ${if (vertical) "Left and right" else "Top and bottom"} swap places.")
            })
        val layers = listOf(listOf(setOf(0, 1, 4), setOf(4, 7, 8)), listOf(setOf(0, 4, 8), setOf(2, 4, 6)), listOf(setOf(1, 3, 4), setOf(4, 5, 7)))
        addWorkshop("visual_mirror", "Katmanları birleştir", "Combine the layers", "Üst üste gelen görsel parçaları bütünleştirme", "Integrate overlapping visual layers",
            ChallengeMode.GRID, LearningMechanic.LAYER_INTEGRATION, layers.map { panels ->
                gridRound("İki saydam katman üst üste gelecek. En az bir katmanda dolu olan kareleri sonuçta işaretle.", "Two transparent layers overlap. Mark result squares filled in at least one layer.", panels, panels.flatten().toSet(), "Örtüşen kareyi iki kez ekleme. Her katmandan görünen bütün parçaları birleştir.", "An overlapping square is included once. Combine the visible parts of both layers.")
            })
        val mosaics = listOf(setOf(0, 1, 3, 4) to setOf(0, 4), setOf(0, 1, 2, 4, 7) to setOf(1, 4), setOf(0, 2, 3, 4, 5, 6, 8) to setOf(0, 2, 4, 8))
        addWorkshop("visual_rule", "Eksik mozaik", "Missing mosaic", "Model ile parçayı görsel olarak karşılaştırma", "Compare a model against an incomplete piece",
            ChallengeMode.GRID, LearningMechanic.MISSING_MOSAIC, mosaics.map { (target, existing) ->
                gridRound("İlk pano tam model, ikinci pano eksik hali. Yalnızca EKSİK kareleri sonuçta işaretle.", "The first board is the full model; the second is incomplete. Mark only the MISSING squares.", listOf(target, existing), target - existing, "Var olan parçaları yeniden seçme. Modelde olup eksik panoda olmayan yerleri tamamla.", "Do not select existing pieces again. Fill positions present in the model but missing from the incomplete board.")
            })
        addWorkshop("alphabet_order", "Harf sırası", "Letter order", "Türkçe harflerin alfabetik konumunu bulma", "Find Turkish alphabetical positions",
            ChallengeMode.ORDER, LearningMechanic.ALPHABET_ORDER, listOf(listOf("C", "Ç", "D"), listOf("H", "I", "İ", "J"), listOf("O", "Ö", "P", "R", "S", "Ş")).map { sequence ->
                r("Harfleri Türkçe alfabedeki sırasına göre diz.", "Put the letters in TURKISH alphabetical order.", sequence, sequence, "Türkçe sıra: ${sequence.joinToString(" → ")}. I/İ ve O/Ö ayrı harflerdir.", "Turkish order: ${sequence.joinToString(" → ")}. I/İ and O/Ö are distinct letters.")
            })
        addWorkshop("syllable_build", "Hece birleştir", "Build with syllables", "Sözcüğü hece parçalarından oluşturma", "Synthesize a word from syllables",
            ChallengeMode.ORDER, LearningMechanic.SYLLABLE_SYNTHESIS, listOf("ARABA" to listOf("A", "RA", "BA"), "KELEBEK" to listOf("KE", "LE", "BEK"), "DOMATES" to listOf("DO", "MA", "TES")).map { (word, sequence) ->
                r("$word sözcüğünü hecelerle oluştur. Bütün heceleri birer kez kullan.", "Build the Turkish word $word. Use each syllable exactly once.", sequence, sequence, "${sequence.joinToString(" + ")} = $word. Yazılı parçaları bir bütün olarak oku.", "${sequence.joinToString(" + ")} = $word. Read the parts together as a word.")
            })
        addWorkshop("numbers_read", "Okunuştan sayıya", "Words to numbers", "Sayı adını doğru rakamla ilişkilendirme", "Connect a number name to digits",
            ChallengeMode.CHOICE, LearningMechanic.NUMBER_NAMING, listOf(0, 64, 100).map { n ->
                val name = com.algokids.data.LearningContent.numberToTurkish(n)
                val options = when (n) { 0 -> listOf("0", "10", "1", "100"); 64 -> listOf("64", "46", "60", "74"); else -> listOf("100", "10", "90", "0") }
                r("“$name” hangi sayıdır?", "Which number is the Turkish name “$name”?", options, listOf("$n"), "$name = $n. Rakam sırası ile sayı adını birlikte kontrol et.", "$name = $n. Check both digit order and the number name.")
            })
        addWorkshop("numbers_place", "Onluk ve birlik", "Tens and units", "Basamak değerini rakam sırasından ayırma", "Distinguish place value from digit order",
            ChallengeMode.CHOICE, LearningMechanic.PLACE_VALUE, listOf(2 to 3, 4 to 0, 3 to 7).map { (tens, ones) ->
                val answer = tens * 10 + ones
                val distractors = listOf(ones * 10 + tens, answer + 10, answer + 1)
                r("$tens onluk ve $ones birlik hangi sayıyı oluşturur?", "Which number has $tens tens and $ones ones?", (listOf(answer) + distractors).distinct().map(Int::toString), listOf("$answer"), "$tens onluk = ${tens * 10}; $ones birlik ile $answer olur. Sıfır birlik de bir basamak bilgisidir.", "$tens tens = ${tens * 10}; adding $ones ones gives $answer. Zero ones is also meaningful.")
            })
    }
    fun forCategory(category: GameCategory) = all.filter { it.category == category }
}
