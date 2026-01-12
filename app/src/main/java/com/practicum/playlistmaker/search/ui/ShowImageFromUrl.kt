package com.practicum.playlistmaker.search.ui

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.practicum.playlistmaker.R

@Composable
fun ShowImageFromUrl(url: String, modifier: Modifier = Modifier) {
    AsyncImage(
        model = url,
        error = painterResource(R.drawable.placeholder),
        contentDescription = null,
        modifier = modifier
            .size(45.dp)
            .clip(RoundedCornerShape(2.dp)),
        contentScale = ContentScale.Crop
    )
}