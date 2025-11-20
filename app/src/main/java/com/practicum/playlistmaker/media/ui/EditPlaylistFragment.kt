package com.practicum.playlistmaker.media.ui

import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import org.koin.androidx.viewmodel.ext.android.viewModel
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentNewPlaylistBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

class EditPlaylistFragment: NewPlaylistFragment() {

    override val viewModel: EditPlaylistViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentNewPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = arguments ?: Bundle()
        title = args.getString(ARGS_TITLE) ?: INPUT_DEF
        description = args.getString(ARGS_DESCRIPTION) ?: INPUT_DEF
        path = args.getString(ARGS_COVER) ?: INPUT_DEF
        val id = args.getInt(ARGS_ID)

        binding.inputTitle.setText(title)
        binding.inputDescription.setText(description)
        binding.albumPic.setImageURI(path.toUri())
        if (path != INPUT_DEF) {
            pic = path.toUri()
            binding.albumPic.setImageURI(pic)
        }
        binding.saveButton.text = getString(R.string.add)
        binding.header.text = getString(R.string.edit)

        binding.saveButton.setOnClickListener {
            lifecycleScope.launch {
                if (isPictureSelected) {
                    viewModel.saveImageToPrivateStorage(pic, timestamp)
                    val filePath = File(
                        requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                        "my_album")
                    val file = File(filePath, "cover_$timestamp.jpg")
                    path = file.absolutePath
                }

                viewModel.saveChanges(title, description, path, id)
                delay(DELAY)
                findNavController().navigateUp()
            }
        }

        binding.albumPic.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.backButton.setOnClickListener {
                findNavController().navigateUp()
        }
        requireActivity().onBackPressedDispatcher.addCallback(object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                    findNavController().navigateUp()
            }
        })
    }

    companion object{
        private const val ARGS_TITLE = "playlist_title"
        private const val ARGS_DESCRIPTION = "playlist_description"
        private const val ARGS_COVER = "playlist_cover"
        private const val ARGS_ID = "playlist_id"

        private const val DELAY: Long = 500

        fun createArgs(playlistTitle: String,
                       playlistDescription: String,
                       playlistCover: String,
                       playlistId: Int): Bundle =
            bundleOf(ARGS_TITLE to playlistTitle,
                ARGS_DESCRIPTION to playlistDescription,
                ARGS_COVER to playlistCover,
                ARGS_ID to playlistId
                )
    }
}