package com.practicum.playlistmaker.ui.settings

import android.content.Intent
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.google.android.material.switchmaterial.SwitchMaterial
import com.practicum.playlistmaker.util.Creator
import com.practicum.playlistmaker.presentation.App
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.use_case.SaveThemeUseCase

class SettingsActivity : AppCompatActivity() {
    private lateinit var themePreferences: SaveThemeUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val backButton = findViewById<ImageView>(R.id.button_back)
        val shareButton = findViewById<FrameLayout>(R.id.button_share)
        val supportButton = findViewById<FrameLayout>(R.id.button_support)
        val eulaButton = findViewById<FrameLayout>(R.id.button_eula)
        val themeSwitcher = findViewById<SwitchMaterial>(R.id.themeSwitcher)

        themePreferences = Creator.provideSaveTheme(this)


        backButton.setOnClickListener {
            finish()
        }

        themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            (applicationContext as App).switchTheme(checked)
            themePreferences.execute(checked)
        }

            shareButton.setOnClickListener {
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