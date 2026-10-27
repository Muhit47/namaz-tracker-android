package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.MainActivity.NamazNavTab
import com.example.ui.theme.FinancyTheme
import com.example.util.HapticUtil

/**
 * iOS-style Frosted Glass Floating Pill Dock Bottom Navigation Bar.
 */
@Composable
fun FinancyFloatingNavBar(
    currentTab: NamazNavTab,
    isBangla: Boolean,
    onTabSelected: (NamazNavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = FinancyTheme.colors
    val dockShape = RoundedCornerShape(999.dp)

    val dockBg = if (colors.isDark) {
        Color(0xFF111C16).copy(alpha = 0.78f)
    } else {
        Color.White.copy(alpha = 0.85f)
    }

    val dockGlassRim = Brush.linearGradient(
        colors = listOf(
            if (colors.isDark) Color.White.copy(alpha = 0.25f) else Color.White.copy(alpha = 0.9f),
            if (colors.isDark) Color.White.copy(alpha = 0.06f) else Color(0xFFCBD5E1).copy(alpha = 0.45f)
        )
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .navigationBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = if (colors.isDark) 18.dp else 12.dp,
                    shape = dockShape,
                    ambientColor = if (colors.isDark) Color(0x60000000) else Color(0x20000000),
                    spotColor = if (colors.isDark) Color(0xFF10B981).copy(alpha = 0.2f) else Color(0x25000000)
                )
                .clip(dockShape)
                .background(dockBg)
                .border(BorderStroke(1.2.dp, dockGlassRim), dockShape),
            color = Color.Transparent,
            shape = dockShape
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp, vertical = 7.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NamazNavTab.entries.forEach { tab ->
                    val isSelected = currentTab == tab
                    FinancyNavItem(
                        tab = tab,
                        isSelected = isSelected,
                        isBangla = isBangla,
                        onClick = { onTabSelected(tab) }
                    )
                }
            }
        }
    }
}

@Composable
private fun FinancyNavItem(
    tab: NamazNavTab,
    isSelected: Boolean,
    isBangla: Boolean,
    onClick: () -> Unit
) {
    val colors = FinancyTheme.colors
    val context = LocalContext.current
    val interactionSource = remember { MutableInteractionSource() }
    val itemShape = RoundedCornerShape(999.dp)

    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.06f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "tab_scale"
    )

    val activePillBrush = if (isSelected) {
        if (colors.isDark) {
            Brush.linearGradient(
                listOf(
                    Color(0xFF10B981).copy(alpha = 0.28f),
                    Color(0xFF059669).copy(alpha = 0.46f)
                )
            )
        } else {
            Brush.linearGradient(
                listOf(
                    Color(0xFF059669).copy(alpha = 0.12f),
                    Color(0xFF059669).copy(alpha = 0.22f)
                )
            )
        }
    } else {
        Brush.linearGradient(listOf(Color.Transparent, Color.Transparent))
    }

    val activeBorder = if (isSelected) {
        BorderStroke(
            1.dp,
            if (colors.isDark) Color(0xFF10B981).copy(alpha = 0.65f) else Color(0xFF059669).copy(alpha = 0.35f)
        )
    } else BorderStroke(0.dp, Color.Transparent)

    val contentColor by animateColorAsState(
        targetValue = if (isSelected) colors.emerald else colors.textMuted,
        label = "tab_content_color"
    )

    Box(
        modifier = Modifier
            .scale(scale)
            .then(
                if (isSelected) {
                    Modifier.shadow(
                        elevation = 4.dp,
                        shape = itemShape,
                        spotColor = colors.emerald.copy(alpha = 0.35f)
                    )
                } else Modifier
            )
            .clip(itemShape)
            .background(activePillBrush)
            .then(if (isSelected) Modifier.border(activeBorder, itemShape) else Modifier)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    HapticUtil.selectionTick(context)
                    onClick()
                }
            )
            .testTag(tab.testTag)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val resId = if (isSelected) tab.filledResId else tab.outlinedResId
            val iconVec = if (isSelected) tab.filledIcon else tab.outlinedIcon

            if (resId != null) {
                Icon(
                    painter = painterResource(id = resId),
                    contentDescription = if (isBangla) tab.titleBn else tab.titleEn,
                    tint = contentColor,
                    modifier = Modifier.size(22.dp)
                )
            } else if (iconVec != null) {
                Icon(
                    imageVector = iconVec,
                    contentDescription = if (isBangla) tab.titleBn else tab.titleEn,
                    tint = contentColor,
                    modifier = Modifier.size(22.dp)
                )
            } else {
                Icon(
                    imageVector = tab.icon,
                    contentDescription = if (isBangla) tab.titleBn else tab.titleEn,
                    tint = contentColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = if (isBangla) tab.titleBn else tab.titleEn,
                fontSize = 10.5.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = contentColor
            )
        }
    }
}
