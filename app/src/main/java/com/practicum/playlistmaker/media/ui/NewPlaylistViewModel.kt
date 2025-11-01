package com.practicum.playlistmaker.media.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.practicum.playlistmaker.media.domain.db.PlaylistInteractor
import com.practicum.playlistmaker.media.domain.models.Playlist
import java.io.File
import java.io.FileOutputStream

open class NewPlaylistViewModel(
    val playlistInteractor: PlaylistInteractor,
    private val context: Context
): ViewModel() {

    fun saveImageToPrivateStorage(uri: Uri, timestamp: Long) {

        val filePath = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "my_album")
        if (!filePath.exists()){
            filePath.mkdirs()
        }
        val contentResolver = context.contentResolver
        val file = File(filePath, "cover_$timestamp.jpg")
        val inputStream = contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)
        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
    }

     fun savePlaylist(title: String,
                     description: String,
                     coverImagePath: String){

        val playlist = Playlist(
            0,
            title = title,
            description = description,
            coverImagePath = coverImagePath,
            trackIds = (Gson().toJson(emptyList<String>())),
            tracksCount = 0
        )

        playlistInteractor.savePlaylist(playlist)
    }

}