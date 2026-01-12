package com.practicum.playlistmaker.media.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.practicum.playlistmaker.search.ui.ySDisplayM
import com.practicum.playlistmaker.settings.ui.SettingsViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaCompose(
    favViewModel: FavoriteViewModel,
    playlistViewModel: PlayListsViewModel,
    settingsViewModel: SettingsViewModel,
    navController: NavController
) {
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { 2 })

    val darkTextStyle = TextStyle(
        fontFamily = ySDisplayM,
        fontSize = 22.sp,
        color = colorResource(com.practicum.playlistmaker.R.color.white),
        fontWeight = FontWeight.W500
    )

    val textStyle = TextStyle(
        fontFamily = ySDisplayM,
        fontSize = 22.sp,
        color = colorResource(com.practicum.playlistmaker.R.color.icon_black),
        fontWeight = FontWeight.W500
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(
                    stringResource(com.practicum.playlistmaker.R.string.button_media),
                    style = if (settingsViewModel.currentTheme()){darkTextStyle} else {textStyle}) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (settingsViewModel.currentTheme()){colorResource(com.practicum.playlistmaker.R.color.icon_black)} else {colorResource(
                        com.practicum.playlistmaker.R.color.white)}
                ),
            )
        }
    ){ padding ->
        Column(
            modifier = Modifier
                .background(if (settingsViewModel.currentTheme()){colorResource(com.practicum.playlistmaker.R.color.icon_black)} else {colorResource(
                    com.practicum.playlistmaker.R.color.white)})
                .fillMaxSize()
                .padding(padding)
        ) {

            SecondaryTabRow(
                selectedTabIndex = pagerState.currentPage,
                modifier = Modifier
                    .background(Color.Green)
                    .fillMaxWidth(),
                indicator = {
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier
                            .tabIndicatorOffset(pagerState.currentPage)
                            .padding(start = 16.dp, end = 16.dp),
                        color = if (settingsViewModel.currentTheme()){colorResource(com.practicum.playlistmaker.R.color.white)} else {colorResource(
                            com.practicum.playlistmaker.R.color.icon_black)})
                },
                divider = { }

            ) {
                Tab(
                    modifier = Modifier
                        .background(if (settingsViewModel.currentTheme()){colorResource(com.practicum.playlistmaker.R.color.icon_black)} else {colorResource(
                        com.practicum.playlistmaker.R.color.white)}),
                    selected = pagerState.currentPage == 0,
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(0)
                        }
                    },
                    text = { Text(stringResource(com.practicum.playlistmaker.R.string.favorite),
                        style = if (settingsViewModel.currentTheme()) {darkTextStyle} else {textStyle},
                        fontSize = 14.sp
                    ) },
                )

                Tab(
                    modifier = Modifier
                        .background(if (settingsViewModel.currentTheme()){colorResource(com.practicum.playlistmaker.R.color.icon_black)} else {colorResource(
                            com.practicum.playlistmaker.R.color.white)}),
                    selected = pagerState.currentPage == 1,
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    unselectedContentColor = MaterialTheme.colorScheme.outline,
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(1)
                        }
                    },
                    text = { Text(stringResource(com.practicum.playlistmaker.R.string.playlists),
                        style = if (settingsViewModel.currentTheme()) {darkTextStyle} else {textStyle},
                        fontSize = 14.sp
                    ) },
                )
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) { page ->
                when(page) {
                    0 -> FavoriteCompose(favViewModel, settingsViewModel.currentTheme(), navController)
                    1 -> PlayListsCompose(playlistViewModel,settingsViewModel.currentTheme(), navController)
                }
            }
        }
    }
}