package com.av.smartkeyboard.ime

import android.content.Context
import android.content.Intent
import android.inputmethodservice.InputMethodService
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
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

        // Settings button
        try {
            val btnSettings = inputViewRoot!!.findViewById<Button>(R.id.btn_settings)
            btnSettings.setOnClickListener {
                val intent = Intent(this, SettingsActivity::class.java).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
                startActivity(intent)
            }
        } catch (_: Exception) { /* ignore if layout simplified */ }

        // AutoType button
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

        // Clipboard button -> paste latest from CLIPHistory
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

        // Paste button -> paste system clipboard
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

        // basic keys wiring (only lowercase letters simplified)
        try {
            val letters = "qwertyuiopasdfghjklzxcvbnm"
            val ids = intArrayOf(
                R.id.key_q,R.id.key_w,R.id.key_e,R.id.key_r,R.id.key_t,R.id.key_y,R.id.key_u,R.id.key_i,R.id.key_o,R.id.key_p,
                R.id.key_a,R.id.key_s,R.id.key_d,R.id.key_f,R.id.key_g,R.id.key_h,R.id.key_j,R.id.key_k,R.id.key_l,
                R.id.key_z,R.id.key_x,R.id.key_c,R.id.key_v,R.id.key_b,R.id.key_n,R.id.key_m
            )
            for (i in letters.indices) {
                val btn = inputViewRoot!!.findViewById<Button>(ids[i])
                val ch = letters[i].toString()
                btn.setOnClickListener { currentInputConnection?.commitText(ch, 1) }
            }
        } catch (_: Exception) {}

        return inputViewRoot!!
    }

    override fun onDestroy() {
        super.onDestroy()
        AutoTypeController.stop()
    }
}
