package com.avsmart.keyboard.ui.wordlist

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.avsmart.keyboard.R
import com.avsmart.keyboard.logic.WordListStore

class WordListEditorActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.wordlist_editor)

        val etInput = findViewById<EditText>(R.id.et_word_input)
        val btnAdd = findViewById<Button>(R.id.btn_add_word)
        val listContainer = findViewById<LinearLayout>(R.id.list_container)

        fun refresh() {
            listContainer.removeAllViews()
            val words = WordListStore.loadWords(this)
            for (w in words) {
                val tv = TextView(this); tv.text = w; tv.setPadding(8,8,8,8)
                tv.setOnClickListener {
                    val list = WordListStore.loadWords(this).toMutableList()
                    list.remove(w); WordListStore.saveWords(this, list.joinToString("\n")); refresh()
                }
                listContainer.addView(tv)
            }
        }

        btnAdd.setOnClickListener {
            val t = etInput.text.toString().trim()
            if (t.isNotEmpty()) {
                val list = WordListStore.loadWords(this).toMutableList()
                list.add(t); WordListStore.saveWords(this, list.joinToString("\n")); etInput.setText(""); refresh()
            }
        }

        refresh()
    }
}
