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
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentNewPlaylistBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import kotlin.getValue

open class NewPlaylistFragment: Fragment() {

    open val viewModel: NewPlaylistViewModel by viewModel()
    var _binding: FragmentNewPlaylistBinding? = null
    val binding get() = _binding!!

    lateinit var confirmDialog: MaterialAlertDialogBuilder
    open var title: String = INPUT_DEF
    open var description: String = INPUT_DEF
    open var path = INPUT_DEF
    open var isPictureSelected = false
    lateinit var pic: Uri

    var timestamp: Long = 0

    val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                pic = uri
                binding.albumPic.setImageURI(uri)
                isPictureSelected = true
            } else {
                isPictureSelected = false
            }
        }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentNewPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        binding.albumPic.setOnClickListener {
            timestamp = System.currentTimeMillis()
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.inputTitle.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                binding.titleMini.setTextColor(ContextCompat.getColor(requireContext(), R.color.bg_blue))
            } else {
                binding.titleMini.setTextColor(ContextCompat.getColor(requireContext(), R.color.edit_text_frame_focus))
            }
        }

        binding.inputDescription.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                binding.descriptionMini.setTextColor(ContextCompat.getColor(requireContext(), R.color.bg_blue))
            } else {
                binding.descriptionMini.setTextColor(ContextCompat.getColor(requireContext(), R.color.edit_text_frame_focus))
            }
        }


        binding.inputTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if ((binding.inputTitle.hasFocus()) && s?.isEmpty() == false) {
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

                if ((binding.inputDescription.hasFocus()) && s?.isEmpty() == false) {
                    binding.titleMini.isVisible = true
                    binding.descriptionMini.isVisible = true
                    binding.inputTitle.setHint(R.string.necessary)
                } else {
                    binding.descriptionMini.isVisible = false
                    binding.inputTitle.setHint(R.string.title)
                }
                description = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {
                binding.apply {
                    binding.inputDescription.setHint(R.string.description)
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
                viewModel.saveImageToPrivateStorage(pic, timestamp)
                val filePath = File(
                    requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                    "my_album"
                )
                val file = File(filePath, "cover_$timestamp.jpg")
                path = file.absolutePath
            }
            viewModel.savePlaylist(title, description, path)
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
        const val INPUT_DEF = ""
    }
}

