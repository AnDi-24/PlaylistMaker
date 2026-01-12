package com.practicum.playlistmaker.media.ui


import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.media.domain.models.Playlist
import com.practicum.playlistmaker.media.ui.model.PlaylistStates
import com.practicum.playlistmaker.playlist.ui.PlaylistFragment
import com.practicum.playlistmaker.search.ui.ShowImageFromUrl
import com.practicum.playlistmaker.search.ui.ySDisplayM
import com.practicum.playlistmaker.search.ui.ySDisplayR

@Composable
fun PlayListsCompose(
    viewModel: PlayListsViewModel,
    isDarkTheme: Boolean,
    navController: NavController
) {

    val playlistState by viewModel.observePlaylist().observeAsState()
    val shouldShowDialog = remember { mutableStateOf(false) }
    var selectedPlaylistId by remember { mutableIntStateOf(0) }

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

    LaunchedEffect(Unit) {
        viewModel.interactor()
    }

    Column() {
        Button(
        onClick ={navController.navigate(R.id.action_mediaFragment_to_newPlaylistFragment)},
        modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .padding(top = 24.dp),
        colors = ButtonDefaults.buttonColors(
            contentColor = if (isDarkTheme){colorResource(R.color.icon_black)} else {colorResource(R.color.white)},
            containerColor = if (isDarkTheme){colorResource(R.color.white)} else {colorResource(R.color.icon_black)})
    ){
        Text(stringResource(R.string.new_playlist))
    }
        when (playlistState) {
            is PlaylistStates.Empty ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .padding(top = 46.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.nothing),
                        contentDescription = null,
                        modifier = Modifier.size(120.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(R.string.no_playlists),
                        style = if (isDarkTheme){darkTextStyle} else {textStyle},
                        fontFamily = ySDisplayM,
                        fontSize = 19.sp,
                        lineHeight = 22.sp,
                        textAlign = TextAlign.Center
                    )
                }
            is PlaylistStates.Content ->
                (playlistState as? PlaylistStates.Content)?.let { content ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxSize().padding(start = 16.dp)
                    ) {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier.align(Alignment.CenterHorizontally ),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(all = 16.dp)) {

                            items(
                                content.playlist,
                                key = { it.id }) { playlist ->

                                PlaylistCard(
                                    modifier = Modifier.pointerInput(Unit) {
                                        detectTapGestures(
                                            onLongPress = {
                                                selectedPlaylistId = playlist.id
                                                shouldShowDialog.value = true
                                            },
                                            onTap = {navController.navigate(R.id.action_mediaFragment_to_playlistFragment,PlaylistFragment.createArgs(playlist.id))})},
                                    if (isDarkTheme){darkTextStyle} else {textStyle},
                                    playlist,
                                    playlist.coverImagePath
                                )
                                SpecialDialog(
                                    visible = shouldShowDialog.value,
                                    onDismissRequest = { shouldShowDialog.value = false },
                                    onConfirmation = {
                                        val playlistToDelete = content.playlist
                                            .find { it.id == selectedPlaylistId }
                                        if (playlistToDelete != null) {
                                            viewModel.deletePlaylist(playlistToDelete)
                                        }
                                        shouldShowDialog.value = false},
                                )
                            }
                        }
                    }
                    }

            else -> {}
        }
    }
}

@Composable
fun PlaylistCard(
    modifier: Modifier,
    textStyle: TextStyle,
    playlist: Playlist,
    imageUrl: String? = null
) {

    val tracksCount = pluralStringResource(
        id = R.plurals.track_count,
        count = playlist.tracksCount,
        playlist.tracksCount
    )
    Row(
        modifier = modifier
            .padding(vertical = 8.dp)

    ) {
        Column() {
            ShowImageFromUrl(imageUrl ?: "", modifier = Modifier.size(160.dp).clip(RoundedCornerShape(8.dp)))

            Text(text = playlist.title,
                modifier = Modifier.padding(top = 4.dp),
                style = textStyle
            )
            Text(text = tracksCount,
                style = textStyle)
        }
    }
}

@Composable
fun SpecialDialog(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
) {
    if (visible) {
        AlertDialog(
            onDismissRequest = { onDismissRequest() },
            text = { Text(text = stringResource(R.string.delete_playlist)) },
            confirmButton = {
                Button(onClick = {onConfirmation() }
                ) {
                    Text(text = stringResource(R.string.yes))
                }
            },
            dismissButton = {
                Button(onClick = { onDismissRequest() }
                ) {
                    Text(text = stringResource(R.string.no))
                }
            }
        )
    }
}



