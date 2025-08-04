package com.practicum.playlistmaker.settings.ui



import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivitySettingsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private val viewModel by viewModel<SettingsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            viewModel.switchTheme(checked)
        }

        binding.buttonShare.setOnClickListener {
            viewModel.sharingButtonClick()
        }

        binding.buttonSupport.setOnClickListener {
            viewModel.supportButtonClick()
        }

        binding.buttonEula.setOnClickListener {
            viewModel.eulaButtonClick()
        }

        binding.buttonBack.setOnClickListener {
            finish()
        }
    }
}