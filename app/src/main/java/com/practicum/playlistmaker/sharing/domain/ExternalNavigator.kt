package com.practicum.playlistmaker.sharing.domain


import com.practicum.playlistmaker.sharing.domain.model.EmailData

interface ExternalNavigator {
    fun shareLink(appLink: String)
    fun openLink(eulaLink: String)
    fun openEmail(support: EmailData)
}