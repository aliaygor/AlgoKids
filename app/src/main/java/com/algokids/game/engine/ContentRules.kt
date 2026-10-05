package com.algokids.game.engine

import com.algokids.game.model.GameContent

object ContentRules {
    /** Pair definitions in the content are authoritative: a context-free association
     * can otherwise mark a wrong pair correct (e.g. a dog with a bone in a homes task). */
    fun checkMatch(content: GameContent, left: String, right: String): Boolean =
        content.questionAssets.orEmpty().zip(content.options.orEmpty()).any { (source,target) ->
            source.equals(left,ignoreCase=true) && target.equals(right,ignoreCase=true)
        }
}
