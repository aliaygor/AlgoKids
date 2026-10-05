package com.algokids.ads

object AdPolicy {
    const val GAP=180_000L
    fun eligible(firstSession:Boolean,completedSinceAd:Int,sessionAge:Long,sinceLastAd:Long,shown:Int) =
        !firstSession && completedSinceAd>=2 && sessionAge>=GAP && sinceLastAd>=GAP && shown<2
}
