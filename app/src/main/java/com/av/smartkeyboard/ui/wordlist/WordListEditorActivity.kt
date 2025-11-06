package com.av.smartkeyboard.ui.wordlist

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.av.smartkeyboard.R
import com.av.smartkeyboard.logic.WordListStore

class WordListEditorActivity : Activity() {
    private fun refresh(listContainer: LinearLayout) {
        listContainer.removeAllViews()
        val words = WordListStore.loadWords(this).toMutableList()
        for (w in words.toList()) {
            val tv = TextView(this)
            tv.text = w
            tv.setPadding(8,8,8,8)
            tv.setOnClickListener {
                words.remove(w)
                WordListStore.saveWords(this, words.joinToString("\n"))
                refresh(listContainer)
            }
            listContainer.addView(tv)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.wordlist_editor)
        val etInput = findViewById<EditText>(R.id.et_word_input)
        val btnAdd = findViewById<Button>(R.id.btn_add_word)
        val listContainer = findViewById<LinearLayout>(R.id.list_container)
        btnAdd.setOnClickListener {
            val t = etInput.text.toString().trim()
            if (t.isNotEmpty()) {
                val words = WordListStore.loadWords(this).toMutableList()
                words.add(t)
                WordListStore.saveWords(this, words.joinToString("\n"))
                etInput.setText("")
                refresh(listContainer)
            }
        }
        refresh(listContainer)
    }
}
