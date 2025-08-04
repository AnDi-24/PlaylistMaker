package com.practicum.playlistmaker.settings.ui



import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivitySettingsBinding
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.settings.domain.use_case.LoadThemeUseCase
import com.practicum.playlistmaker.settings.domain.use_case.SaveThemeUseCase
import com.practicum.playlistmaker.sharing.domain.api.SharingInteractor
import com.practicum.playlistmaker.util.Creator

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var themeSavePreferences: SaveThemeUseCase
    private lateinit var themeLoadPreferences: LoadThemeUseCase
    private lateinit var sharingInteractor: SharingInteractor
    private var viewModel: SettingsViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        themeSavePreferences = Creator.provideSaveTheme(this)
        themeLoadPreferences = Creator.provideLoadTheme(this)
        sharingInteractor = Creator.provideSharingInteractor(this)

        viewModel = ViewModelProvider(this, SettingsViewModel.getFactory(
            sharingInteractor,
            applicationContext as App,
            themeLoadPreferences,
            themeSavePreferences))
            .get(SettingsViewModel::class.java)

        binding.themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            viewModel?.switchTheme(checked)
        }

        binding.buttonShare.setOnClickListener {
            viewModel?.sharingButtonClick()
        }

        binding.buttonSupport.setOnClickListener {
            viewModel?.supportButtonClick()
        }

        binding.buttonEula.setOnClickListener {
            viewModel?.eulaButtonClick()
        }

        binding.buttonBack.setOnClickListener {
            finish()
        }
    }
}