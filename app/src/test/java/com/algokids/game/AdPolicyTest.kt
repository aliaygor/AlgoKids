package com.algokids.game
import com.algokids.ads.AdPolicy
import org.junit.Assert.*
import org.junit.Test
class AdPolicyTest {
 @Test fun interstitialsWaitForLearningBreakAndFrequencyLimitsEvenInFirstSession() {
  assertFalse(AdPolicy.eligible(0,999999,999999,0))
  assertFalse(AdPolicy.eligible(2,1000,999999,0))
  assertFalse(AdPolicy.eligible(2,999999,1000,0))
  assertFalse(AdPolicy.eligible(2,999999,999999,AdPolicy.MAX_PER_SESSION))
  assertTrue(AdPolicy.eligible(1,120000,120000,0))
  assertTrue(AdPolicy.eligible(1,120000,120000,2))
 }
}
