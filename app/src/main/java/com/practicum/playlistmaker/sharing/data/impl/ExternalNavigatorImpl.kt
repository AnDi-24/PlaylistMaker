package com.practicum.playlistmaker.sharing.data.impl

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import com.practicum.playlistmaker.sharing.domain.ExternalNavigator
import com.practicum.playlistmaker.sharing.domain.model.EmailData

class ExternalNavigatorImpl(val context: Context): ExternalNavigator {

    override fun share(content: String) {
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.putExtra(Intent.EXTRA_TEXT, content)
        shareIntent.type = "text/plain"
        val messageIntent = Intent.createChooser(shareIntent, null)
        messageIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(messageIntent)
    }

    override fun openLink(eulaLink: String) {
        val browseIntent = Intent()
        browseIntent.action = Intent.ACTION_VIEW
        browseIntent.data = eulaLink.toUri()
        browseIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(browseIntent)
    }

    override fun openEmail(support: EmailData ) {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SENDTO
        sendIntent.data = "mailto:".toUri()
        sendIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(support.emailAddress))
        sendIntent.putExtra(Intent.EXTRA_TEXT, support.emailText)
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, support.emailSubject)
        sendIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(sendIntent)
    }
}