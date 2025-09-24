package com.practicum.playlistmaker.settings.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.practicum.playlistmaker.databinding.FragmentSettingsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<SettingsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}