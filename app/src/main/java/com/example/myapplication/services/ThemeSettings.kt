package com.example.myapplication.services

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

object ThemeSettings {

    private const val PREFS = "settings"
    private const val KEY_DARK = "dark_mode"

    /** Áp dụng lựa chọn đã lưu. Nếu người dùng chưa chọn lần nào thì giữ theo hệ thống. */
    fun applySaved(context: Context) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        if (prefs.contains(KEY_DARK)) {
            apply(prefs.getBoolean(KEY_DARK, false))
        }
    }

    fun setDarkMode(context: Context, enabled: Boolean) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_DARK, enabled)
            .apply()
        apply(enabled)
    }

    private fun apply(enabled: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (enabled) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}