package com.practicum.playlistmaker.media.ui

import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentNewPlaylistBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import kotlin.getValue

class NewPlaylistFragment: Fragment() {

    private val newPlaylistViewModel: NewPlaylistViewModel by viewModel()
    private var _binding: FragmentNewPlaylistBinding? = null
    private val binding get() = _binding!!
    lateinit var confirmDialog: MaterialAlertDialogBuilder
    var title: String = INPUT_DEF
    var description: String = INPUT_DEF
    var isPictureSelected = false
    lateinit var pic: Uri
    var path = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentNewPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    val filePath = File(
                        requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                        "my_album"
                    )
                    val file = File(filePath, "cover_$title.jpg")
                    path = file.absolutePath
                    pic = uri
                    binding.albumPic.setImageURI(uri)
                    isPictureSelected = true
                } else {
                    isPictureSelected = false
                }
            }

        binding.albumPic.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.inputTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if ((binding.inputTitle.hasFocus() || binding.inputDescription.hasFocus()) && s?.isEmpty() == false) {
                    binding.titleMini.isVisible = true
                    binding.descriptionMini.isVisible = true
                    binding.inputDescription.hint = ""
                } else {
                    binding.titleMini.isVisible = false
                    binding.descriptionMini.isVisible = false
                    binding.inputDescription.setHint(R.string.description)
                }

                title = s.toString()
                binding.saveButton.isEnabled = s?.isNotEmpty() == true
            }

            override fun afterTextChanged(s: Editable?) {
                binding.apply {
                }
            }
        })

        binding.inputDescription.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if ((binding.inputTitle.hasFocus() || binding.inputDescription.hasFocus()) && s?.isEmpty() == false) {
                    binding.titleMini.isVisible = true
                    binding.descriptionMini.isVisible = true
                    binding.inputTitle.setHint(R.string.necessary)
                } else {
                    binding.titleMini.isVisible = false
                    binding.descriptionMini.isVisible = false
                    binding.inputTitle.setHint(R.string.title)
                }
                description = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {
                binding.apply {
                }
            }
        })

        confirmDialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.finished_title))
            .setMessage(getString(R.string.finished_title_message))
            .setNegativeButton(R.string.cancel) { dialog, which ->
            }
            .setPositiveButton(R.string.finish) { dialog, which ->
                findNavController().navigateUp()
            }

        binding.saveButton.setOnClickListener {
            if (isPictureSelected) {
                newPlaylistViewModel.saveImageToPrivateStorage(pic, title)
                val filePath = File(
                    requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                    "my_album"
                )
                val file = File(filePath, "cover_$title.jpg")
                path = file.absolutePath
            }
            newPlaylistViewModel.savePlaylist(title, description, path)
            Toast.makeText(
                context,
                context?.getString(R.string.playlist) + " " + title + " " + context?.getString(R.string.created),
                Toast.LENGTH_SHORT
            ).show()
            findNavController().navigateUp()
        }

        binding.backButton.setOnClickListener {
            if (!title.isEmpty() || !description.isEmpty() || isPictureSelected) {
                confirmDialog.show()
            } else {
                findNavController().navigateUp()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (!title.isEmpty() || !description.isEmpty() || isPictureSelected) {
                    confirmDialog.show()
                }else{
                    findNavController().navigateUp()
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        requireActivity().onBackPressedDispatcher.addCallback(object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (!title.isEmpty() || !description.isEmpty() || isPictureSelected) {
                    confirmDialog.show()
                }else{
                    findNavController().navigateUp()
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        title = ""
        description = ""
        isPictureSelected = false
        _binding = null
    }

    companion object {
        private const val INPUT_DEF = ""
    }
}

