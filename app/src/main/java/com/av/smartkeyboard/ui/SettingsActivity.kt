package com.av.smartkeyboard.ui

import android.app.Activity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Switch
import com.av.smartkeyboard.R
import com.av.smartkeyboard.ui.wordlist.WordListEditorActivity

class SettingsActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        val prefs = getSharedPreferences("avsmart_prefs", MODE_PRIVATE)
        val switchTheme = findViewById<Switch>(R.id.switch_theme)
        val switchAuto = findViewById<Switch>(R.id.switch_autotype)
        val etDelay = findViewById<EditText>(R.id.et_delay_setting)
        val spAfter = findViewById<Spinner>(R.id.sp_after)
        val btnWordlist = findViewById<Button>(R.id.btn_wordlist)
        switchTheme.isChecked = prefs.getBoolean("dark_theme", true)
        switchAuto.isChecked = prefs.getBoolean("enable_autotype", true)
        etDelay.setText(prefs.getInt("delay_ms", 500).toString())
        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrayOf("space","enter"))
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spAfter.adapter = adapter
        spAfter.setSelection(if ((prefs.getString("after_action","space") ?: "space") == "space") 0 else 1)
        switchTheme.setOnCheckedChangeListener { _, checked -> prefs.edit().putBoolean("dark_theme", checked).apply() }
        switchAuto.setOnCheckedChangeListener { _, checked -> prefs.edit().putBoolean("enable_autotype", checked).apply() }
        btnWordlist.setOnClickListener { startActivity(android.content.Intent(this, WordListEditorActivity::class.java)) }
    }

    override fun onPause() {
        super.onPause()
        val prefs = getSharedPreferences("avsmart_prefs", MODE_PRIVATE)
        val etDelay = findViewById<EditText>(R.id.et_delay_setting)
        val spAfter = findViewById<Spinner>(R.id.sp_after)
        val delayVal = try { etDelay.text.toString().toInt() } catch (_: Exception) { 500 }
        prefs.edit().putInt("delay_ms", delayVal).putString("after_action", if (spAfter.selectedItemPosition==0) "space" else "enter").apply()
    }
}
