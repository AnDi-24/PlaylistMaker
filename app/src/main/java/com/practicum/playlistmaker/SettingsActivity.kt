package com.practicum.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val backButton = findViewById<ImageView>(R.id.button_back)
        val shareButton = findViewById<FrameLayout>(R.id.button_share)
        val supportButton = findViewById<FrameLayout>(R.id.button_support)
        val eulaButton = findViewById<FrameLayout>(R.id.button_eula)

        backButton.setOnClickListener {
            finish()
        }

        shareButton.setOnClickListener{
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.link_share))
            shareIntent.type = "text/plain"

            val messageIntent = Intent.createChooser(shareIntent, null)
            startActivity(messageIntent)
        }

        supportButton.setOnClickListener {
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SENDTO
            sendIntent.data = "mailto:".toUri()
            sendIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.mail_support)))
            sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.text_support))
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.subject_support))
            startActivity(sendIntent)
        }

        eulaButton.setOnClickListener {
            val browseIntent = Intent()
            browseIntent.action = Intent.ACTION_VIEW
            browseIntent.data = getString(R.string.link_eula).toUri()
            startActivity(browseIntent)
        }
    }
}