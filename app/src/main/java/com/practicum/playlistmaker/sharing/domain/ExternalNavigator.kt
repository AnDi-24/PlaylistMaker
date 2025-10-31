package com.practicum.playlistmaker.sharing.domain


import com.practicum.playlistmaker.sharing.domain.model.EmailData

interface ExternalNavigator {
    fun share(content: String)
    fun openLink(eulaLink: String)
    fun openEmail(support: EmailData)
}