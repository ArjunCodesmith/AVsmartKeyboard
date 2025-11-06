package com.av.smartkeyboard.ime

import android.content.Context
import android.content.Intent
import android.inputmethodservice.InputMethodService
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.av.smartkeyboard.R
import com.av.smartkeyboard.logic.AutoTypeController
import com.av.smartkeyboard.logic.WordListStore
import com.av.smartkeyboard.ui.SettingsActivity
import com.av.smartkeyboard.ui.clip.CLIPHistory

class AVSmartIME : InputMethodService() {

    private var inputViewRoot: View? = null
    private val mainHandler = Handler(Looper.getMainLooper())

    override fun onCreateInputView(): View {
        val inflater = LayoutInflater.from(this)
        inputViewRoot = inflater.inflate(R.layout.keyboard_view, null)

        try {
            val btnSettings = inputViewRoot!!.findViewById<Button>(R.id.btn_settings)
            btnSettings.setOnClickListener {
                val intent = Intent(this, SettingsActivity::class.java).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
                startActivity(intent)
            }
        } catch (_: Exception) {}

        try {
            val btnAuto = inputViewRoot!!.findViewById<Button>(R.id.btn_autotype)
            btnAuto.setOnClickListener {
                val prefs = getSharedPreferences("avsmart_prefs", Context.MODE_PRIVATE)
                if (!prefs.getBoolean("enable_autotype", true)) {
                    Toast.makeText(this, "AutoType disabled in settings", Toast.LENGTH_SHORT).show(); return@setOnClickListener
                }
                if (AutoTypeController.isRunning()) {
                    AutoTypeController.stop(); Toast.makeText(this, "AutoType stopped", Toast.LENGTH_SHORT).show(); return@setOnClickListener
                }
                val list = WordListStore.loadWords(this)
                if (list.isEmpty()) { Toast.makeText(this, "Word list empty", Toast.LENGTH_SHORT).show(); return@setOnClickListener }
                val delayMs = getSharedPreferences("avsmart_prefs", Context.MODE_PRIVATE).getInt("delay_ms", 500)
                val after = getSharedPreferences("avsmart_prefs", Context.MODE_PRIVATE).getString("after_action", "space") ?: "space"
                AutoTypeController.start(list, delayMs, after) { text, kind ->
                    val ic = currentInputConnection ?: return@start
                    mainHandler.post {
                        when (kind) {
                            "text" -> ic.commitText(text, 1)
                            "space" -> ic.commitText(" ", 1)
                            "enter" -> {
                                ic.sendKeyEvent(android.view.KeyEvent(android.view.KeyEvent.ACTION_DOWN, android.view.KeyEvent.KEYCODE_ENTER))
                                ic.sendKeyEvent(android.view.KeyEvent(android.view.KeyEvent.ACTION_UP, android.view.KeyEvent.KEYCODE_ENTER))
                            }
                        }
                    }
                }
            }
        } catch (_: Exception) {}

        try {
            val btnClip = inputViewRoot!!.findViewById<Button>(R.id.btn_clipboard)
            btnClip.setOnClickListener {
                val ic = currentInputConnection
                val latest = CLIPHistory.peek()
                if (latest != null && ic != null) {
                    ic.commitText(latest, 1)
                } else {
                    Toast.makeText(this, "Clipboard empty", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (_: Exception) {}

        try {
            val btnPaste = inputViewRoot!!.findViewById<Button>(R.id.btn_paste)
            btnPaste.setOnClickListener {
                val ic = currentInputConnection ?: return@setOnClickListener
                val cm = getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                val text = cm.primaryClip?.getItemAt(0)?.coerceToText(this)?.toString()
                if (!text.isNullOrEmpty()) {
                    ic.commitText(text, 1)
                    CLIPHistory.record(text)
                }
            }
        } catch (_: Exception) {}

        return inputViewRoot!!
    }

    override fun onDestroy() {
        super.onDestroy()
        AutoTypeController.stop()
    }
}
