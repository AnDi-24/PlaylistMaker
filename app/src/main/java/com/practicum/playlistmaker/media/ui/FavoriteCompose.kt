package com.practicum.playlistmaker.media.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.media.ui.model.FavoriteStates
import com.practicum.playlistmaker.player.ui.PlayerFragment
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.TrackItem
import com.practicum.playlistmaker.search.ui.ySDisplayM
import com.practicum.playlistmaker.search.ui.ySDisplayR
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

@Composable
fun FavoriteCompose(
    viewModel: FavoriteViewModel,
    isDarkTheme: Boolean,
    navController: NavController
) {
    val favoriteState by viewModel.observeFavorite().observeAsState()

    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.interactor()
    }

    val darkTextStyle = TextStyle(
        fontFamily = ySDisplayR,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        color = colorResource(R.color.white),
        fontWeight = FontWeight.Normal
    )

    val textStyle = TextStyle(
        fontFamily = ySDisplayR,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        color = colorResource(R.color.icon_black),
        fontWeight = FontWeight.Normal
    )

    when (favoriteState) {
        is FavoriteStates.Empty ->
            Column(
                modifier = Modifier
                    .background(if (isDarkTheme){colorResource(R.color.icon_black)} else {colorResource(
                        R.color.white)})
                    .fillMaxSize()
                    .padding(16.dp)
                    .padding(top = 106.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.nothing),
                    contentDescription = null,
                    modifier = Modifier.size(120.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.media_is_empty),
                    style = if (isDarkTheme){darkTextStyle} else {textStyle},
                    fontFamily = ySDisplayM,
                    fontSize = 19.sp,
                    lineHeight = 22.sp,
                    textAlign = TextAlign.Center
                )
            }
        is FavoriteStates.Content ->
            (favoriteState as? FavoriteStates.Content)?.let { content ->
                Column(modifier = Modifier
                    .background(if (isDarkTheme){colorResource(R.color.icon_black)} else {colorResource(
                        R.color.white)})
                    .padding(start = 13.dp, end = 12.dp, top = 16.dp)
                    .fillMaxHeight()) {
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 500.dp)
                    ) {
                        items(content.tracks, key = { it.trackId }) { track ->
                            TrackItem(
                                track = track,
                                isDarkTheme = isDarkTheme,
                                onClick = { clickedTrack ->
                                    scope.launch {
                                        track.isFavorite = true
                                        val json = Json.encodeToString(Track.serializer(), track)
                                        navController.navigate(
                                            R.id.action_mediaFragment_to_playerFragment,
                                            PlayerFragment.createArgs(json)
                                        )
                                    }
                                }
                            )
                        }

                    }
                }
            }
        else -> {}
    }
}






