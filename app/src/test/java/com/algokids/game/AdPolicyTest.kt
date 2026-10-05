package com.algokids.game
import com.algokids.ads.AdPolicy
import org.junit.Assert.*
import org.junit.Test
class AdPolicyTest {
 @Test fun adsOnlyAppearAfterEnoughLearningAndNeverOnFirstSession() {
  assertFalse(AdPolicy.eligible(true,10,999999,999999,0))
  assertFalse(AdPolicy.eligible(false,1,999999,999999,0))
  assertFalse(AdPolicy.eligible(false,2,1000,999999,0))
  assertFalse(AdPolicy.eligible(false,2,999999,1000,0))
  assertFalse(AdPolicy.eligible(false,2,999999,999999,2))
  assertTrue(AdPolicy.eligible(false,2,180000,180000,0))
 }
}
