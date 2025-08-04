package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.search.data.impl.TrackRepositoryImpl
import com.practicum.playlistmaker.search.domain.api.TracksRepository
import org.koin.dsl.module

val repositoryModule = module {

    single<TracksRepository> {
        TrackRepositoryImpl(get())
    }
}
