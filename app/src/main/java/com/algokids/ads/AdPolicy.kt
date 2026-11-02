package com.algokids.ads

object AdPolicy {
    const val MAX_PER_SESSION=3
    const val GAP=120_000L
    fun eligible(firstSession:Boolean,completedSinceAd:Int,sessionAge:Long,sinceLastAd:Long,shown:Int) =
        !firstSession && completedSinceAd>=1 && sessionAge>=GAP && sinceLastAd>=GAP && shown<MAX_PER_SESSION
}
