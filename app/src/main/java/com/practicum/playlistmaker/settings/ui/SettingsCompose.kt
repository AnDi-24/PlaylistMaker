package com.practicum.playlistmaker.settings.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.practicum.playlistmaker.R

@Composable
fun SettingsCompose(viewModel: SettingsViewModel){

    val ySDisplay = FontFamily(
        Font(R.font.ys_display_medium)
    )

    val darkTextStyle = TextStyle(
        fontFamily = ySDisplay,
        fontSize = 16.sp,
        lineHeight = 29.sp,
        color = colorResource(R.color.white),
        fontWeight = FontWeight.Normal
    )

    val textStyle = TextStyle(
        fontFamily = ySDisplay,
        fontSize = 16.sp,
        lineHeight = 29.sp,
        color = colorResource(R.color.black),
        fontWeight = FontWeight.Normal
    )
    val scrollState = rememberScrollState()

    val isDarkTheme = remember { mutableStateOf(viewModel.currentTheme()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isDarkTheme.value){colorResource(R.color.icon_black)} else {colorResource(R.color.white)})
            .verticalScroll(scrollState)
    ) {

        SettingsHeader(
            text = stringResource(R.string.button_settings),
            style = if (isDarkTheme.value){darkTextStyle} else {textStyle}
        )

        SettingsSwitchItem(
            text = stringResource(R.string.dark_theme),
            style = if (isDarkTheme.value){darkTextStyle} else {textStyle},
            isChecked = viewModel.currentTheme(),
            onCheckedChange = {
                viewModel.switchTheme(it)
                isDarkTheme.value = it
            }
        )

        SettingsClickableItem(
            text = stringResource(R.string.button_share),
            style = if (isDarkTheme.value){darkTextStyle} else {textStyle},
            icon = painterResource(id = R.drawable.share),
            tint = if (isDarkTheme.value){colorResource(R.color.white)} else {colorResource(R.color.grey)},
            onClick = { viewModel.sharingButtonClick() }
        )

        SettingsClickableItem(
            text = stringResource(R.string.button_support),
            style = if (isDarkTheme.value){darkTextStyle} else {textStyle},
            icon = painterResource(id = R.drawable.support),
            tint = if (isDarkTheme.value){colorResource(R.color.white)} else {colorResource(R.color.grey)},
            onClick = { viewModel.supportButtonClick() }
        )

        SettingsClickableItem(
            text = stringResource(R.string.button_eula),
            style = if (isDarkTheme.value){darkTextStyle} else {textStyle},
            icon = painterResource(id = R.drawable.right_arrow),
            tint = if (isDarkTheme.value){colorResource(R.color.white)} else {colorResource(R.color.grey)},
            onClick = { viewModel.eulaButtonClick() }
        )
    }
}

@Composable
fun SettingsSwitchItem(
    text: String, isChecked: Boolean, style: TextStyle, onCheckedChange: (Boolean) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 45.dp)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = text,
            style = style,
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults
                .colors(
                    checkedThumbColor = colorResource(R.color.bg_blue),
                    checkedTrackColor = colorResource(R.color.track_blue),
                    uncheckedThumbColor = colorResource(R.color.grey),
                    uncheckedTrackColor = colorResource(R.color.track_white)
                )
        )
    }
}

@Composable
fun SettingsClickableItem
            (text: String, style: TextStyle, icon: Painter, tint: Color, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .clickable {onClick()}
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 21.dp)) {

        Text(
            style = style,
            text = text
        )
        Icon(
            painter = icon,
            tint = tint,
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun SettingsHeader(text: String, style: TextStyle) {

    Text(
        text = text,
        style = style,
        fontSize = 22.sp,
        modifier = Modifier
            .padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
            .fillMaxWidth()
    )
}
