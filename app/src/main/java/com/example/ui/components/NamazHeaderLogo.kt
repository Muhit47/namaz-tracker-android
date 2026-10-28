package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.theme.FinancyTheme

/**
 * Official Brand Header Logo adapting dynamically to Dark & Light glass modes.
 * Displays the Squircle NL Icon + Namaz Tracker + "দৈনন্দিন নামায ও কাযা ট্র্যাকিং".
 */
@Composable
fun NamazHeaderLogo(
    isBangla: Boolean = true,
    modifier: Modifier = Modifier
) {
    val colors = FinancyTheme.colors
    val logoRes = if (colors.isDark) {
        R.drawable.ic_app_brand_logo_dark
    } else {
        R.drawable.ic_app_brand_logo_light
    }

    Image(
        painter = painterResource(id = logoRes),
        contentDescription = "Namaz Tracker Brand Logo",
        contentScale = ContentScale.Fit,
        modifier = modifier.height(36.dp)
    )
}
