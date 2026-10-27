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
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.FinancyTheme
import com.example.util.HapticUtil

/**
 * iOS-style Frosted Glass & Rounded Pill Button with tactile bouncy spring press animation and haptic feedback.
 */
@Composable
fun IosGlassPillButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isPrimary: Boolean = true,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    fontSize: TextUnit = 13.sp,
    contentPadding: PaddingValues = PaddingValues(horizontal = 22.dp, vertical = 11.dp),
    enabled: Boolean = true
) {
    val colors = FinancyTheme.colors
    val context = LocalContext.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Bouncy spring scale when pressed
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "ios_button_scale"
    )

    val shape = RoundedCornerShape(999.dp)

    // Glass Background and Rim Brush
    val backgroundBrush = if (isPrimary) {
        if (colors.isDark) {
            Brush.linearGradient(
                colors = listOf(
                    Color(0xFF10B981).copy(alpha = 0.88f),
                    Color(0xFF059669).copy(alpha = 0.96f)
                )
            )
        } else {
            Brush.linearGradient(
                colors = listOf(
                    Color(0xFF059669).copy(alpha = 0.92f),
                    Color(0xFF047857).copy(alpha = 0.98f)
                )
            )
        }
    } else {
        if (colors.isDark) {
            Brush.linearGradient(
                colors = listOf(
                    Color(0xFF1A2821).copy(alpha = 0.75f),
                    Color(0xFF131D18).copy(alpha = 0.85f)
                )
            )
        } else {
            Brush.linearGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.85f),
                    Color(0xFFF1F5F9).copy(alpha = 0.95f)
                )
            )
        }
    }

    val rimBrush = if (isPrimary) {
        Brush.linearGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.45f),
                Color.White.copy(alpha = 0.12f)
            )
        )
    } else {
        Brush.linearGradient(
            colors = listOf(
                if (colors.isDark) Color.White.copy(alpha = 0.22f) else Color.White.copy(alpha = 0.85f),
                if (colors.isDark) Color.White.copy(alpha = 0.05f) else Color(0xFFCBD5E1).copy(alpha = 0.4f)
            )
        )
    }

    val contentColor = if (isPrimary) {
        Color.White
    } else {
        if (colors.isDark) Color(0xFFE2E8F0) else Color(0xFF0F172A)
    }

    val elevation = if (isPrimary) (if (colors.isDark) 10.dp else 6.dp) else 4.dp
    val spotColor = if (isPrimary) colors.emerald.copy(alpha = 0.4f) else Color.Black.copy(alpha = 0.2f)

    Box(
        modifier = modifier
            .scale(scale)
            .shadow(
                elevation = elevation,
                shape = shape,
                ambientColor = Color.Black.copy(alpha = 0.2f),
                spotColor = spotColor
            )
            .clip(shape)
            .background(backgroundBrush)
            .border(BorderStroke(1.2.dp, rimBrush), shape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = {
                    HapticUtil.lightTick(context)
                    onClick()
                }
            )
            .padding(contentPadding),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (leadingIcon != null) {
                leadingIcon()
                Spacer(modifier = Modifier.width(6.dp))
            }

            Text(
                text = text,
                fontSize = fontSize,
                fontWeight = FontWeight.Bold,
                color = contentColor
            )

            if (trailingIcon != null) {
                Spacer(modifier = Modifier.width(6.dp))
                trailingIcon()
            }
        }
    }
}

/**
 * iOS-style Frosted Glass Capsule Chip for Waqt status, tags, filters, and categories.
 */
@Composable
fun IosGlassChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: (@Composable () -> Unit)? = null,
    activeColor: Color = FinancyTheme.colors.emerald,
    fontSize: TextUnit = 11.sp,
    contentPadding: PaddingValues = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
) {
    val colors = FinancyTheme.colors
    val context = LocalContext.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "ios_chip_scale"
    )

    val shape = RoundedCornerShape(999.dp)

    val bgBrush = if (isSelected) {
        if (colors.isDark) {
            Brush.linearGradient(
                colors = listOf(
                    activeColor.copy(alpha = 0.28f),
                    activeColor.copy(alpha = 0.48f)
                )
            )
        } else {
            Brush.linearGradient(
                colors = listOf(
                    activeColor.copy(alpha = 0.12f),
                    activeColor.copy(alpha = 0.24f)
                )
            )
        }
    } else {
        if (colors.isDark) {
            Brush.linearGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.06f),
                    Color.White.copy(alpha = 0.03f)
                )
            )
        } else {
            Brush.linearGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.75f),
                    Color(0xFFF8FAFC).copy(alpha = 0.9f)
                )
            )
        }
    }

    val rimBrush = if (isSelected) {
        Brush.linearGradient(
            colors = listOf(
                activeColor.copy(alpha = 0.85f),
                activeColor.copy(alpha = 0.5f)
            )
        )
    } else {
        Brush.linearGradient(
            colors = listOf(
                if (colors.isDark) Color.White.copy(alpha = 0.16f) else Color.White.copy(alpha = 0.8f),
                if (colors.isDark) Color.White.copy(alpha = 0.05f) else Color(0xFFCBD5E1).copy(alpha = 0.35f)
            )
        )
    }

    val textColor by animateColorAsState(
        targetValue = if (isSelected) activeColor else colors.textMuted,
        label = "ios_chip_text"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .shadow(
                elevation = if (isSelected) 4.dp else 1.dp,
                shape = shape,
                spotColor = if (isSelected) activeColor.copy(alpha = 0.3f) else Color.Transparent
            )
            .clip(shape)
            .background(bgBrush)
            .border(BorderStroke(1.dp, rimBrush), shape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    HapticUtil.lightTick(context)
                    onClick()
                }
            )
            .padding(contentPadding),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (leadingIcon != null) {
                leadingIcon()
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
                text = text,
                fontSize = fontSize,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = textColor
            )
        }
    }
}

/**
 * iOS-style Segmented Control in a Frosted Glass Pill Dock.
 */
@Composable
fun <T> IosGlassSegmentedControl(
    items: List<T>,
    selectedItem: T,
    onItemSelected: (T) -> Unit,
    labelProvider: @Composable (T) -> String,
    modifier: Modifier = Modifier,
    iconProvider: (@Composable (T, Color) -> Unit)? = null
) {
    val colors = FinancyTheme.colors
    val context = LocalContext.current
    val dockShape = RoundedCornerShape(999.dp)

    val dockBg = if (colors.isDark) {
        Color(0xFF09100C).copy(alpha = 0.65f)
    } else {
        Color(0xFFE2E8F0).copy(alpha = 0.6f)
    }

    val dockBorder = if (colors.isDark) {
        Color.White.copy(alpha = 0.12f)
    } else {
        Color.White.copy(alpha = 0.7f)
    }

    Box(
        modifier = modifier
            .clip(dockShape)
            .background(dockBg)
            .border(BorderStroke(1.dp, dockBorder), dockShape)
            .padding(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val isSelected = item == selectedItem
                val itemShape = RoundedCornerShape(999.dp)
                val interactionSource = remember { MutableInteractionSource() }

                val itemScale by animateFloatAsState(
                    targetValue = if (isSelected) 1.0f else 0.98f,
                    label = "seg_scale"
                )

                val itemBg = if (isSelected) {
                    if (colors.isDark) {
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF10B981).copy(alpha = 0.85f),
                                Color(0xFF059669).copy(alpha = 0.95f)
                            )
                        )
                    } else {
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF059669).copy(alpha = 0.9f),
                                Color(0xFF047857).copy(alpha = 0.98f)
                            )
                        )
                    }
                } else {
                    Brush.linearGradient(listOf(Color.Transparent, Color.Transparent))
                }

                val itemRim = if (isSelected) {
                    BorderStroke(1.dp, Color.White.copy(alpha = 0.35f))
                } else {
                    BorderStroke(0.dp, Color.Transparent)
                }

                val textColor = if (isSelected) Color.White else colors.textMuted

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .scale(itemScale)
                        .then(
                            if (isSelected) {
                                Modifier.shadow(4.dp, itemShape, spotColor = colors.emerald.copy(alpha = 0.4f))
                            } else Modifier
                        )
                        .clip(itemShape)
                        .background(itemBg)
                        .then(if (isSelected) Modifier.border(itemRim, itemShape) else Modifier)
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null,
                            onClick = {
                                if (!isSelected) {
                                    HapticUtil.selectionTick(context)
                                    onItemSelected(item)
                                }
                            }
                        )
                        .padding(vertical = 7.dp, horizontal = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        if (iconProvider != null) {
                            iconProvider(item, textColor)
                            Spacer(modifier = Modifier.width(6.dp))
                        }
                        Text(
                            text = labelProvider(item),
                            fontSize = 11.5.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = textColor
                        )
                    }
                }
            }
        }
    }
}
