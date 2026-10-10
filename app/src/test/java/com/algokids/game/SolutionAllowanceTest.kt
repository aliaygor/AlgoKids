package com.algokids.game
import android.content.SharedPreferences
import com.algokids.data.SolutionAllowance
import java.lang.reflect.Proxy
import org.junit.Assert.*
import org.junit.Test
class SolutionAllowanceTest {
    @Test fun fiveDailyUsesPersistAndRewardAddsOnlyOneUse() {
        val values=mutableMapOf<String,Any>()
        val editor=Proxy.newProxyInstance(SharedPreferences.Editor::class.java.classLoader,arrayOf(SharedPreferences.Editor::class.java)) { proxy,m,a ->
            when(m.name) {
                "putString","putInt" -> { values[a!![0] as String]=a[1];proxy }
                "commit" -> true
                else -> error(m.name)
            }
        } as SharedPreferences.Editor
        val prefs=Proxy.newProxyInstance(SharedPreferences::class.java.classLoader,arrayOf(SharedPreferences::class.java)) { _,m,a ->
            when(m.name) {"getString","getInt" -> values[a!![0]] ?: a[1];"edit" -> editor;else -> error(m.name)}
        } as SharedPreferences
        var date="2026-10-10"
        val quota=SolutionAllowance(prefs) {date}
        assertEquals(5,quota.remaining())
        repeat(5) {assertTrue(quota.use())}
        assertFalse(quota.use())
        assertEquals(0,SolutionAllowance(prefs) {date}.remaining())
        quota.reward();assertEquals(1,quota.remaining());assertTrue(quota.use());assertFalse(quota.use())
        date="2026-10-11";assertEquals(5,quota.remaining())
    }
}
