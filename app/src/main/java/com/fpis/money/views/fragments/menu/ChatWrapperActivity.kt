package com.fpis.money.views.fragments.menu

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.fpis.money.R
import advanced.lab.chatlibrary.ChatLauncher

class ChatWrapperActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_wrapper)

        val backButton = findViewById<ImageButton>(R.id.back_button)
        backButton.setOnClickListener {
            finish()
        }

        ChatLauncher.start(this)
    }
}