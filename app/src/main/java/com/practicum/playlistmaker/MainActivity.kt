package com.practicum.playlistmaker

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val searchButton = findViewById<Button>(R.id.button_search)
        val mediaButton = findViewById<Button>(R.id.button_media)
        val settingsButton = findViewById<Button>(R.id.button_settings)

        val imageClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                Toast.makeText(this@MainActivity, "Поиск", Toast.LENGTH_SHORT).show()
            }
        }
        searchButton.setOnClickListener(imageClickListener)

        mediaButton.setOnClickListener { Toast.makeText(this@MainActivity, "Медиатека", Toast.LENGTH_SHORT).show()
        }

        settingsButton.setOnClickListener { Toast.makeText(this@MainActivity, "Настройки",  Toast.LENGTH_SHORT).show()
        }
    }
}