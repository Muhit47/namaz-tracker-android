package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.NamazNavTab
import com.example.ui.theme.FinancyTheme

@Composable
fun FinancyFloatingNavBar(
    currentTab: NamazNavTab,
    isBangla: Boolean,
    onTabSelected: (NamazNavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = FinancyTheme.colors

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
                    elevation = if (colors.isDark) 16.dp else 10.dp,
                    shape = RoundedCornerShape(28.dp),
                    ambientColor = if (colors.isDark) Color(0x60000000) else Color(0x20000000),
                    spotColor = if (colors.isDark) Color(0x80000000) else Color(0x25000000)
                )
                .clip(RoundedCornerShape(28.dp))
                .background(colors.navBackground)
                .border(1.dp, colors.navBorder, RoundedCornerShape(28.dp)),
            color = colors.navBackground,
            shape = RoundedCornerShape(28.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp, vertical = 8.dp),
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
    val interactionSource = remember { MutableInteractionSource() }

    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.08f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "tab_scale"
    )

    val activePillBg by animateColorAsState(
        targetValue = if (isSelected) colors.emeraldSoft else Color.Transparent,
        label = "tab_bg"
    )

    val contentColor by animateColorAsState(
        targetValue = if (isSelected) colors.emerald else colors.textMuted,
        label = "tab_content_color"
    )

    Box(
        modifier = Modifier
            .scale(scale)
            .clip(RoundedCornerShape(20.dp))
            .background(activePillBg)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .testTag(tab.testTag)
            .padding(horizontal = 10.dp, vertical = 6.dp),
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
