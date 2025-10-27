package com.practicum.playlistmaker.search.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.practicum.playlistmaker.media.data.db.dao.PlaylistDao
import com.practicum.playlistmaker.media.data.db.dao.TracksOnPlaylistsDao
import com.practicum.playlistmaker.media.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.media.data.db.entity.TracksOnPlaylistsEntity
import com.practicum.playlistmaker.search.data.db.dao.TrackDao
import com.practicum.playlistmaker.search.data.db.entity.TrackEntity

@Database(version = 5, entities = [TrackEntity::class, PlaylistEntity::class, TracksOnPlaylistsEntity::class], exportSchema = false)
abstract class AppDatabase: RoomDatabase() {

    abstract fun trackDao(): TrackDao

    abstract fun playlistDao(): PlaylistDao

    abstract fun tracksOnPlaylistDao(): TracksOnPlaylistsDao

}