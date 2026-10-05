package com.algokids.game.model
import com.algokids.data.LearningContent
object ChallengeLocalization {
 fun localize(c:Challenge,english:Boolean):Challenge {
  if(!english)return c
  return when(c.id) {
   "alphabet_order" -> c.copy(skillEn="Find English alphabetical positions",rounds=listOf(listOf("C","D","E"),listOf("H","I","J","K"),listOf("O","P","Q","R","S","T")).map { letters ->
    ChallengeRound("","Put the letters in ENGLISH alphabetical order.",letters,letters,"","English order: ${letters.joinToString(" → ")}.")
   })
   "syllable_build" -> c.copy(titleEn="Build a word",skillEn="Join word parts",rounds=listOf("RAINBOW" to listOf("RAIN","BOW"),"BUTTERFLY" to listOf("BUT","TER","FLY"),"SUNFLOWER" to listOf("SUN","FLOW","ER")).map { (word,parts) ->
    ChallengeRound("","Build $word. Use each word part once.",parts,parts,"","${parts.joinToString(" + ")} = $word. Read the parts together.")
   })
   "numbers_read" -> c.copy(rounds=c.rounds.zip(listOf(0,64,100)).map { (r,n) -> r.copy(promptEn="Which number is ${LearningContent.numberToEnglish(n)}?",explanationEn="${LearningContent.numberToEnglish(n)} = $n. Check the digit order.") })
   else -> c
  }
 }
}
