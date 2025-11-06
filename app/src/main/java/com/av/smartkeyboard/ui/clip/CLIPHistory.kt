package com.av.smartkeyboard.ui.clip

import android.os.Handler
import android.os.Looper
import java.util.ArrayDeque

object CLIPHistory {
    private val items = ArrayDeque<Pair<String, Long>>()
    private val handler = Handler(Looper.getMainLooper())

    fun record(text: String) {
        val now = System.currentTimeMillis()
        items.addFirst(text to now)
        while (items.size > 5) items.removeLast()
        handler.postDelayed({ trim() }, 120_000)
    }

    private fun trim() {
        val cutoff = System.currentTimeMillis() - 120_000
        while (items.isNotEmpty() && items.last.second < cutoff) items.removeLast()
    }

    fun peek(): String? {
        trim()
        return items.firstOrNull()?.first
    }

    fun all(): List<String> {
        trim()
        return items.map { it.first }
    }
}
