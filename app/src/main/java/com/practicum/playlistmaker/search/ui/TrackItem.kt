package com.practicum.playlistmaker.search.ui

import android.icu.text.SimpleDateFormat
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.search.domain.models.Track
import java.util.Locale

@Composable
fun TrackItem(
    track: Track,
    isDarkTheme: Boolean,
    onClick: (Track) -> Unit
) {
    val darkTextStyle = TextStyle(
        fontFamily = ySDisplayR,
        fontSize = 16.sp,
        lineHeight = 19.sp,
        color = colorResource(R.color.white),
        fontWeight = FontWeight.Normal
    )

    val textStyle = TextStyle(
        fontFamily = ySDisplayR,
        fontSize = 16.sp,
        lineHeight = 19.sp,
        color = colorResource(R.color.icon_black),
        fontWeight = FontWeight.Normal
    )

    val artistDarkTextStyle = TextStyle(
        fontFamily = ySDisplayR,
        fontSize = 11.sp,
        lineHeight = 13.sp,
        color = colorResource(R.color.white),
        fontWeight = FontWeight.Normal
    )

    val artistTextStyle = TextStyle(
        fontFamily = ySDisplayR,
        fontSize = 11.sp,
        lineHeight = 13.sp,
        color = colorResource(R.color.grey),
        fontWeight = FontWeight.Normal
    )
    val formatter = SimpleDateFormat("mm:ss", Locale.getDefault())
    Row(
        modifier = Modifier
            .background(if (isDarkTheme){colorResource(R.color.icon_black)} else {colorResource(R.color.white)})
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick(track) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        ShowImageFromUrl(track.artworkUrl100)

        Spacer(modifier = Modifier.width(8.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                track.trackName,
                style = if(isDarkTheme){darkTextStyle} else {textStyle},
                maxLines = 1,
                overflow = TextOverflow.Ellipsis)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    track.artistName,
                    style = if(isDarkTheme){artistDarkTextStyle} else {artistTextStyle},
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Icon(
                    painterResource(R.drawable.dot),
                    tint = if(isDarkTheme){colorResource(R.color.white)} else {colorResource(R.color.grey)},
                    contentDescription = null
                )
                Text(
                    text = formatter.format(track.trackTimeMillis.toLong()),
                    style = if(isDarkTheme){artistDarkTextStyle} else {artistTextStyle},)
            }
        }
        Icon(
            painterResource(R.drawable.right_arrow),
            tint = if(isDarkTheme){colorResource(R.color.white)} else {colorResource(R.color.grey)},
            contentDescription = null,
        )
    }
}