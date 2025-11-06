package com.av.smartkeyboard.logic

import android.content.Context

object WordListStore {
    private const val PREFS = "avsmart_prefs"
    private const val KEY = "wordlist_text"

    fun loadWords(ctx: Context): List<String> {
        val prefs = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val custom = prefs.getString(KEY, null)
        val text = if (!custom.isNullOrEmpty()) custom else {
            try { ctx.assets.open("wordlist.txt").bufferedReader().use { it.readText() } }
            catch (_: Exception) { "" }
        }
        return text.split('\n').map { it.trim() }.filter { it.isNotEmpty() }
    }

    fun saveWords(ctx: Context, all: String) {
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(KEY, all).apply()
    }
}
