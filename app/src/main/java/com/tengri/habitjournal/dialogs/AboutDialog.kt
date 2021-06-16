package com.tengri.habitjournal.dialogs

import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.Toast
import com.tengri.habitjournal.R


class AboutDialog(context: Context) : Dialog(context) {

    init {
        setCancelable(true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_about)

        val button = findViewById<Button>(R.id.mailButton)
        button.setOnClickListener {
            val mailto = "mailto:spac3ious@gmail.com" +
                    "?cc=" +
                    "&subject=" + Uri.encode("Feature Request") +
                    "&body=" + Uri.encode("Can you implement this feature?")
            val emailIntent = Intent(Intent.ACTION_SENDTO)
            emailIntent.data = Uri.parse(mailto)
            try {
                context.startActivity(emailIntent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(context, "Sorry, Could not find the mail app..", Toast.LENGTH_SHORT).show()
            }
        }

//        window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

    }
}