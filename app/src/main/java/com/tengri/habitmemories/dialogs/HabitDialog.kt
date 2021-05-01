package com.tengri.habitmemories.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import com.google.android.material.textfield.TextInputEditText
import com.tengri.habitmemories.R

class HabitDialog(context: Context, private val editTextString: String = "") : Dialog(context) {

    private lateinit var onSubmit : (text: String) -> Unit

    init {
        setCancelable(true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_add_habit)

        window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        val inputText = findViewById<TextInputEditText>(R.id.inputText)
        val submitButton = findViewById<Button>(R.id.submitButton)

        inputText.setText(editTextString)

        submitButton.setOnClickListener {
            this.onSubmit(inputText.text.toString())
            this.dismiss()
        }
    }

    fun setOnSubmit(func: (text: String) -> Unit) {
        this.onSubmit = func
    }
}