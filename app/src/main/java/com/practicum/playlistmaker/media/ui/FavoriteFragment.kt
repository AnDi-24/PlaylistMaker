package com.practicum.playlistmaker.media.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.practicum.playlistmaker.databinding.FavoriteFragmentBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteFragment: Fragment() {

    private val favoriteViewModel: FavoriteViewModel by viewModel()

    private lateinit var binding: FavoriteFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FavoriteFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    companion object {
        fun newInstance() = FavoriteFragment().apply {
            arguments = Bundle().apply {}
        }
    }
}