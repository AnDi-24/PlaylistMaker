package com.practicum.playlistmaker.search.ui

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.player.ui.PlayerFragment
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.model.SearchState
import com.practicum.playlistmaker.settings.ui.SettingsViewModel
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

val ySDisplayM = FontFamily(
    Font(R.font.ys_display_medium)
)

val ySDisplayR = FontFamily(
    Font(R.font.ys_display_regular)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchCompose(
    viewModel: SearchViewModel,
    themeViewModel: SettingsViewModel,
    navController: NavController
) {
    val state by viewModel.observeState().observeAsState()
    val historyState by viewModel.state.collectAsState()
    val toastState by viewModel.observeShowToast().observeAsState()

    var query by rememberSaveable { mutableStateOf("") }
    var inputValue by remember { mutableStateOf(query) }
    var isFocused by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    var refreshHistory by remember { mutableStateOf(false) }
    val isDarkTheme = remember { mutableStateOf(themeViewModel.currentTheme()) }

    LaunchedEffect(refreshHistory) {
        viewModel.loadHistory()
    }
    LaunchedEffect(Unit) {
        if (query.isNotEmpty()) {
            viewModel.requestState(query)
        }
    }

    val darkTextStyle = TextStyle(
        fontFamily = ySDisplayM,
        fontSize = 22.sp,
        lineHeight = 26.sp,
        color = colorResource(R.color.white),
        fontWeight = FontWeight.Normal
    )

    val textStyle = TextStyle(
        fontFamily = ySDisplayM,
        fontSize = 22.sp,
        lineHeight = 26.sp,
        color = colorResource(R.color.icon_black),
        fontWeight = FontWeight.Normal
    )

    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(
                    stringResource(R.string.button_search),
                    style = if (isDarkTheme.value){darkTextStyle} else {textStyle}) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (isDarkTheme.value){colorResource(R.color.icon_black)} else {colorResource(R.color.white)}
                ),
            )
        }
    ) { padding ->
        Column(modifier = Modifier
            .background(if (isDarkTheme.value){colorResource(R.color.icon_black)} else {colorResource(R.color.white)})
            .padding(padding)
            .fillMaxSize()
            ) {
            TextField(
                value = inputValue,
                onValueChange = { newValue ->
                    inputValue = newValue
                    query = newValue
                    viewModel.requestState(query)
                    viewModel.searchDebounce(newValue)
                    isFocused = !isFocused
                },
                textStyle = TextStyle(fontSize = 16.sp,
                    lineHeight = 16.sp),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .height(64.dp)
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
                    .clickable {
                        focusRequester.requestFocus()
                    }
                    .onFocusChanged { focusState ->
                        isFocused = focusState.isFocused
                    }
                    .padding(horizontal = 16.dp, vertical = 8.dp),

                placeholder = { Text(stringResource(R.string.button_search),
                    modifier = Modifier
                        .fillMaxHeight(),
                    style = TextStyle(
                        fontFamily = ySDisplayR,
                        fontSize = 16.sp,
                        color = if (isDarkTheme.value) {colorResource(R.color.icon_black)} else{colorResource(R.color.grey)},
                        fontWeight = FontWeight.Normal
                    ))},
                leadingIcon = {Icon(
                    modifier = Modifier.size(16.dp),
                    painter = painterResource(id = R.drawable.search),
                    tint = if (isDarkTheme.value){colorResource(R.color.icon_black)} else {colorResource(R.color.grey)},
                    contentDescription = null
                )},
                trailingIcon = {
                    if (inputValue.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                query = ""
                                inputValue = ""
                                viewModel.clearButton()
                            }
                        ) {
                            Icon(painter = painterResource(id = R.drawable.clear), contentDescription = null)
                        }
                    }
                },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = colorResource(R.color.track_white),
                    unfocusedContainerColor = colorResource(R.color.track_white),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            if (inputValue.isEmpty()){
                viewModel.clearTracks()
            }

            if (isFocused && inputValue.isEmpty()) {
                val historyList = when (historyState) {
                    is SearchState.History -> (historyState as SearchState.History).tracks
                    else -> emptyList()
                }
                if (historyList.isNotEmpty()) {
                    Column(
                        modifier = Modifier
                            .padding(top = 42.dp)
                            .padding(horizontal = 16.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            stringResource(R.string.searched),
                            style  = if (isDarkTheme.value){darkTextStyle} else {textStyle},
                            fontSize = 19.sp,
                            lineHeight = 22.sp,
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                                .padding(bottom = 12.dp)
                            .align(Alignment.CenterHorizontally)
                        )
                        LazyColumn(
                            modifier = Modifier.heightIn(max = 500.dp)
                        ) {
                            items(historyList) { track ->
                                TrackItem(
                                    track = track,
                                    isDarkTheme = isDarkTheme.value,
                                    onClick = { clickedTrack ->
                                        query = ""
                                        scope.launch {
                                            val json =  Json.encodeToString(Track.serializer(), track)
                                            navController.navigate(
                                                R.id.action_searchFragment_to_playerFragment,
                                                PlayerFragment.createArgs(json)
                                            )
                                        }
                                    }
                                )
                            }
                        }
                    }

                    Button(
                        onClick = {
                            viewModel.clearHistory()
                            refreshHistory = !refreshHistory
                                  },
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(top = 16.dp),
                        colors = ButtonDefaults.buttonColors(
                            contentColor = if (isDarkTheme.value){colorResource(R.color.icon_black)} else {colorResource(R.color.white)},
                            containerColor = if (isDarkTheme.value){colorResource(R.color.white)} else {colorResource(R.color.icon_black)}
                        )
                    ) {
                        Text(stringResource(R.string.clear_history_button))
                    }
                }
            }

            when (state) {
                is SearchState.Loading -> LoadingState()
                is SearchState.Error -> ErrorState(
                    if (isDarkTheme.value){darkTextStyle} else {textStyle},
                    isDarkTheme = isDarkTheme.value,
                    onRetry = { viewModel.requestState(inputValue) })
                is SearchState.Empty -> EmptyState(if (isDarkTheme.value){darkTextStyle} else {textStyle})
                is SearchState.Content -> ContentState(
                    tracks = ((state as SearchState.Content).tracks).filterNotNull(),
                    isDarkTheme = isDarkTheme.value,
                    onTrackClick = { track ->
                        viewModel.addToHistory(track)
                        scope.launch {
                            val json = Json.encodeToString(Track.serializer(), track)
                                navController.navigate(
                                    R.id.action_searchFragment_to_playerFragment,
                                    PlayerFragment.createArgs(json)
                                )
                        }
                    }
                )
                else -> {}
            }

            if (toastState != null) {
                ShowToast(toastState)
            }
        }
    }
}

@Composable
fun LoadingState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorState(
    textStyle: TextStyle,
    isDarkTheme: Boolean,
    onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(top = 102.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.went_wrong),
            contentDescription = null,
            modifier = Modifier.size(120.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.something_went_wrong),
            style = textStyle,
            fontSize = 19.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onRetry,
            modifier = Modifier
                .height(36.dp),
            colors = ButtonDefaults.buttonColors(
                contentColor = if (isDarkTheme){colorResource(R.color.icon_black)} else {colorResource(R.color.white)},
                containerColor = if (isDarkTheme){colorResource(R.color.white)} else {colorResource(R.color.icon_black)}
            )
        ) {
            Text(stringResource(R.string.refresh))
        }
    }
}

@Composable
fun EmptyState(
    textStyle: TextStyle,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(top = 102.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.nothing),
            contentDescription = null,
            modifier = Modifier.size(120.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.nothing_found),
            style = textStyle,
            fontSize = 19.sp,
            lineHeight = 22.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ContentState(
    tracks: List<Track>,
    isDarkTheme: Boolean,
    onTrackClick: (Track) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 13.dp, vertical = 8.dp)
    ) {
        items(tracks) { track ->
            TrackItem(
                track = track,
                isDarkTheme = isDarkTheme,
                onClick = onTrackClick
            )

        }
    }
}

@Composable
fun ShowToast(text: String?) {
    val context = LocalContext.current
    Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
}



