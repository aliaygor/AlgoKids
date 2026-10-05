package com.algokids.game

import com.algokids.game.model.*
import com.algokids.game.engine.ContentRules
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import org.junit.Assert.*
import org.junit.Test

class AssetContentTest {
    private val items:List<GameContent> = Gson().fromJson(File("src/main/assets/educational_content.json").readText(),object:TypeToken<List<GameContent>>(){}.type)

    @Test fun allQuestionsHaveUniqueIdsAndUsableAnswersAndBilingualInstructions() {
        assertEquals(items.size,items.map { it.id }.distinct().size)
        items.forEach { item ->
            assertNotNull(item.type)
            assertNotNull(item.category)
            assertFalse(item.instruction.isNullOrBlank())
            assertFalse(item.instructionEn.isNullOrBlank())
            assertFalse(item.options.isNullOrEmpty())
            if(item.answer!="MATCH_ALL") assertTrue(item.id,item.answer in item.options.orEmpty())
            else {
                assertEquals(item.id,item.questionAssets.orEmpty().size,item.options.orEmpty().size)
                item.questionAssets.orEmpty().zip(item.options.orEmpty()).forEach { (left,right) -> assertTrue(ContentRules.checkMatch(item,left,right)) }
            }
        }
    }

    @Test fun numericalCountingMatchesVisibleObjectCount() {
        items.filter { it.type==GameType.COUNTING && it.category==GameCategory.NUMERICAL }.forEach {
            assertEquals(it.id,it.questionAssets.orEmpty().size.toString(),it.answer)
        }
    }

    @Test fun starSidesAreNotConfusedWithOuterPoints() {
        val sides=items.first { it.id=="g6" }
        assertTrue(ContentRules.checkMatch(sides,"STAR_SHAPE","10"))
        assertFalse(ContentRules.checkMatch(sides,"STAR_SHAPE","5"))
        assertEquals("5",items.first { it.id=="g10" }.answer)
    }
}
