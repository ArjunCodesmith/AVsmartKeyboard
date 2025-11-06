package com.avsmart.keyboard.logic

import kotlinx.coroutines.*

object AutoTypeController {
    private var job: Job? = null

    fun start(words: List<String>, delayMs: Int, after: String, emit: (String, String) -> Unit) {
        job?.cancel()
        job = CoroutineScope(Dispatchers.Default).launch {
            for (w in words) {
                if (!isActive) break
                emit(w, "text")
                if (after == "space") emit("", "space") else emit("", "enter")
                delay(delayMs.toLong())
            }
        }
    }

    fun stop() { job?.cancel() }
    fun isRunning() = job?.isActive == true
}
