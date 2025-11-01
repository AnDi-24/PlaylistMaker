package com.practicum.playlistmaker.sharing.domain.impl

import android.content.Context
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.search.data.converters.TrackDbConverter
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.sharing.domain.api.SharingInteractor
import com.practicum.playlistmaker.sharing.domain.ExternalNavigator
import com.practicum.playlistmaker.sharing.domain.model.EmailData
import com.practicum.playlistmaker.sharing.domain.model.TrackToShare

class SharingInteractorImpl (
    private val context: Context,
    private val externalNavigator: ExternalNavigator,
    private val trackDbConverter: TrackDbConverter
    ) : SharingInteractor {

    override fun share(content: String) {
        externalNavigator.share(content)
    }

    override fun openTerms() {
        externalNavigator.openLink(getTermsLink())
    }

    override fun openSupport() {
        externalNavigator.openEmail(getSupportEmailData())
    }

    override fun getTracksToShare(tracks: List<Track>): List<TrackToShare> {
        return tracks.map { tracks -> trackDbConverter.trackToShare(tracks) }
    }

    private fun getSupportEmailData(): EmailData {
        return EmailData(
            emailAddress = context.getString(R.string.mail_support),
            emailText = context.getString(R.string.text_support),
            emailSubject = context.getString(R.string.subject_support)
        )
    }

    private fun getTermsLink(): String {
        return context.getString(R.string.link_eula)
    }


}