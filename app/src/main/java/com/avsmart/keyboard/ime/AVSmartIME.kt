package com.avsmart.keyboard.ime

import android.content.Intent
import android.inputmethodservice.InputMethodService
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import com.avsmart.keyboard.R
import com.avsmart.keyboard.logic.AutoTypeController
import com.avsmart.keyboard.logic.WordListStore
import com.avsmart.keyboard.ui.SettingsActivity
import com.avsmart.keyboard.ui.clip.CLIPHistory

class AVSmartIME : InputMethodService() {
    private var inputView: View? = null
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreateInputView(): View {
        val inflater = LayoutInflater.from(this)
        inputView = inflater.inflate(R.layout.keyboard_view, null)

        inputView!!.findViewById<Button>(R.id.btn_settings).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) })
        }

        inputView!!.findViewById<Button>(R.id.btn_autotype).setOnClickListener {
            val prefs = getSharedPreferences("avsmart_prefs", MODE_PRIVATE)
            if (!prefs.getBoolean("enable_autotype", true)) return@setOnClickListener
            val list = WordListStore.loadWords(this)
            if (list.isEmpty()) return@setOnClickListener
            val delay = prefs.getInt("delay_ms", 800)
            val after = prefs.getString("after_action", "space") ?: "space"
            AutoTypeController.start(list, delay, after) { text, kind ->
                val ic = currentInputConnection ?: return@start
                handler.post {
                    when(kind) {
                        "text" -> ic.commitText(text, 1)
                        "space" -> ic.commitText(" ", 1)
                        else -> {
                            ic.sendKeyEvent(android.view.KeyEvent(android.view.KeyEvent.ACTION_DOWN, android.view.KeyEvent.KEYCODE_ENTER))
                            ic.sendKeyEvent(android.view.KeyEvent(android.view.KeyEvent.ACTION_UP, android.view.KeyEvent.KEYCODE_ENTER))
                        }
                    }
                }
            }
        }

        inputView!!.findViewById<Button>(R.id.btn_clipboard).setOnClickListener {
            val ic = currentInputConnection ?: return@setOnClickListener
            val latest = CLIPHistory.peek()
            if (latest != null) ic.commitText(latest, 1)
        }

        inputView!!.findViewById<Button>(R.id.btn_paste).setOnClickListener {
            val ic = currentInputConnection ?: return@setOnClickListener
            val text = android.content.ClipboardManager::class.java // placeholder for actual paste logic
        }

        return inputView!!
    }

    override fun onDestroy() {
        super.onDestroy()
        AutoTypeController.stop()
    }
}
