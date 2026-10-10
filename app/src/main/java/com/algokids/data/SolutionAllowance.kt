package com.algokids.data

import android.content.SharedPreferences
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Shared across both languages. Viewing a worked example never spends an allowance. */
class SolutionAllowance(private val prefs: SharedPreferences, private val today: () -> String = {
    SimpleDateFormat("yyyy-MM-dd",Locale.ROOT).format(Date())
}) {
    private fun refresh() {
        val day=today()
        if(prefs.getString("solution_day",null)!=day) prefs.edit().putString("solution_day",day)
            .putInt("solution_used",0).putInt("solution_bonus",0).commit()
    }
    fun remaining(): Int { refresh(); return (5+prefs.getInt("solution_bonus",0)-prefs.getInt("solution_used",0)).coerceAtLeast(0) }
    fun use(): Boolean {
        if(remaining()==0)return false
        return prefs.edit().putInt("solution_used",prefs.getInt("solution_used",0)+1).commit()
    }
    fun reward() { refresh();prefs.edit().putInt("solution_bonus",prefs.getInt("solution_bonus",0)+1).commit() }
}
