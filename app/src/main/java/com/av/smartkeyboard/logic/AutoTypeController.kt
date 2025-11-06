package com.av.smartkeyboard.logic

import kotlinx.coroutines.*

object AutoTypeController {
    private var job: Job? = null

    fun isRunning() = job?.isActive == true
    fun stop() { job?.cancel() }

    fun start(words: List<String>, delayMs: Int, after: String, emit: (String, String) -> Unit) {
        stop()
        job = CoroutineScope(Dispatchers.Default).launch {
            for (w in words) {
                if (!isActive) break
                emit(w, "text")
                when (after) {
                    "space" -> emit("", "space")
                    else -> emit("", "enter")
                }
                delay(delayMs.toLong())
            }
        }
    }
}
