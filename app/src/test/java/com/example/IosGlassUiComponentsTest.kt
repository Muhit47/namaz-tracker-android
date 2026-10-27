package com.example

import android.content.Context
import android.os.Vibrator
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import androidx.test.core.app.ApplicationProvider
import com.example.ui.components.IosGlassChip
import com.example.ui.components.IosGlassPillButton
import com.example.ui.components.IosGlassSegmentedControl
import com.example.ui.theme.FinancyTheme
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class IosGlassUiComponentsTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // ==========================================
    // 1. IosGlassPillButton Tests
    // ==========================================

    @Test
    fun `primary IosGlassPillButton renders text and responds to click`() {
        var clicked = false

        composeTestRule.setContent {
            FinancyTheme {
                IosGlassPillButton(
                    text = "Offer Namaz",
                    isPrimary = true,
                    onClick = { clicked = true },
                    modifier = Modifier.testTag("primary_pill_btn")
                )
            }
        }

        // Verify button node exists and displays text
        composeTestRule.onNodeWithTag("primary_pill_btn").assertExists()
        composeTestRule.onNodeWithText("Offer Namaz").assertIsDisplayed()

        // Perform click and assert callback execution
        composeTestRule.onNodeWithTag("primary_pill_btn").performClick()
        assertTrue("Primary pill button click callback must be triggered", clicked)
    }

    @Test
    fun `secondary IosGlassPillButton renders and triggers callback`() {
        var clickedCount = 0

        composeTestRule.setContent {
            FinancyTheme {
                IosGlassPillButton(
                    text = "Missed",
                    isPrimary = false,
                    onClick = { clickedCount++ },
                    modifier = Modifier.testTag("secondary_pill_btn")
                )
            }
        }

        composeTestRule.onNodeWithTag("secondary_pill_btn").assertExists()
        composeTestRule.onNodeWithText("Missed").assertIsDisplayed()

        composeTestRule.onNodeWithTag("secondary_pill_btn").performClick()
        composeTestRule.onNodeWithTag("secondary_pill_btn").performClick()
        assertEquals("Secondary pill button should record both clicks", 2, clickedCount)
    }

    @Test
    fun `disabled IosGlassPillButton ignores click interactions`() {
        var clicked = false

        composeTestRule.setContent {
            FinancyTheme {
                IosGlassPillButton(
                    text = "Locked Waqt",
                    enabled = false,
                    onClick = { clicked = true },
                    modifier = Modifier.testTag("disabled_pill_btn")
                )
            }
        }

        composeTestRule.onNodeWithTag("disabled_pill_btn").assertExists()
        composeTestRule.onNodeWithTag("disabled_pill_btn").assertIsNotEnabled()

        // Attempt click
        composeTestRule.onNodeWithTag("disabled_pill_btn").performClick()
        assertFalse("Disabled pill button must not trigger onClick callback", clicked)
    }

    @Test
    fun `IosGlassPillButton displays leading and trailing icons`() {
        composeTestRule.setContent {
            FinancyTheme {
                IosGlassPillButton(
                    text = "With Icons",
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "leading_check",
                            modifier = Modifier.testTag("leading_icon")
                        )
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "trailing_bell",
                            modifier = Modifier.testTag("trailing_icon")
                        )
                    },
                    onClick = {}
                )
            }
        }

        composeTestRule.onNodeWithTag("leading_icon", useUnmergedTree = true).assertExists()
        composeTestRule.onNodeWithTag("trailing_icon", useUnmergedTree = true).assertExists()
        composeTestRule.onNodeWithText("With Icons").assertIsDisplayed()
    }

    // ==========================================
    // 2. IosGlassChip Tests
    // ==========================================

    @Test
    fun `IosGlassChip toggles selection and executes onClick callback`() {
        var isSelected = false
        var clickCount = 0

        composeTestRule.setContent {
            FinancyTheme {
                IosGlassChip(
                    text = "Fajr Alarm",
                    isSelected = isSelected,
                    onClick = {
                        isSelected = !isSelected
                        clickCount++
                    },
                    modifier = Modifier.testTag("test_glass_chip")
                )
            }
        }

        val chipNode = composeTestRule.onNodeWithTag("test_glass_chip")
        chipNode.assertExists()
        composeTestRule.onNodeWithText("Fajr Alarm").assertIsDisplayed()

        // Click once
        chipNode.performClick()
        assertEquals("Chip click callback must be triggered once", 1, clickCount)
        assertTrue("Chip state should transition to selected", isSelected)
    }

    @Test
    fun `IosGlassChip renders with leading icon`() {
        composeTestRule.setContent {
            FinancyTheme {
                IosGlassChip(
                    text = "Qaza Mode",
                    isSelected = true,
                    leadingIcon = {
                        Box(modifier = Modifier.size(10.dp).testTag("chip_icon"))
                    },
                    onClick = {}
                )
            }
        }

        composeTestRule.onNodeWithTag("chip_icon", useUnmergedTree = true).assertExists()
        composeTestRule.onNodeWithText("Qaza Mode").assertIsDisplayed()
    }

    // ==========================================
    // 3. IosGlassSegmentedControl Tests
    // ==========================================

    @Test
    fun `IosGlassSegmentedControl renders all items and allows switching`() {
        val soundModes = listOf("full_azan", "short_takbeer", "system_tone", "silent")
        var currentSelection = "full_azan"

        composeTestRule.setContent {
            var selected by remember { mutableStateOf(currentSelection) }

            FinancyTheme {
                IosGlassSegmentedControl(
                    items = soundModes,
                    selectedItem = selected,
                    onItemSelected = {
                        selected = it
                        currentSelection = it
                    },
                    labelProvider = { mode ->
                        when (mode) {
                            "full_azan" -> "Full Azan"
                            "short_takbeer" -> "Takbeer"
                            "system_tone" -> "Tone"
                            else -> "Silent"
                        }
                    },
                    modifier = Modifier.testTag("segmented_sound_selector")
                )
            }
        }

        // Verify all segments are displayed
        composeTestRule.onNodeWithText("Full Azan").assertIsDisplayed()
        composeTestRule.onNodeWithText("Takbeer").assertIsDisplayed()
        composeTestRule.onNodeWithText("Tone").assertIsDisplayed()
        composeTestRule.onNodeWithText("Silent").assertIsDisplayed()

        // Switch to Takbeer
        composeTestRule.onNodeWithText("Takbeer").performClick()
        assertEquals("Selection should switch to Takbeer", "short_takbeer", currentSelection)

        // Switch to Silent
        composeTestRule.onNodeWithText("Silent").performClick()
        assertEquals("Selection should switch to Silent", "silent", currentSelection)
    }

    @Test
    fun `IosGlassSegmentedControl does not invoke callback when clicking already selected item`() {
        val options = listOf("Daily", "Weekly", "Monthly")
        var callbackInvocationCount = 0

        composeTestRule.setContent {
            FinancyTheme {
                IosGlassSegmentedControl(
                    items = options,
                    selectedItem = "Daily",
                    onItemSelected = {
                        callbackInvocationCount++
                    },
                    labelProvider = { it }
                )
            }
        }

        // Clicking the already active item "Daily"
        composeTestRule.onNodeWithText("Daily").performClick()
        assertEquals("Callback should not be called when re-selecting already active item", 0, callbackInvocationCount)

        // Clicking "Weekly"
        composeTestRule.onNodeWithText("Weekly").performClick()
        assertEquals("Callback should be invoked when choosing a new option", 1, callbackInvocationCount)
    }

    // ==========================================
    // 4. Haptic Vibrator Service Integration
    // ==========================================

    @Test
    fun `IosGlass component interactions execute without throwing haptic exceptions`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        assertNotNull("System vibrator service should be non-null in Robolectric environment", vibrator)

        var pillClicked = false
        composeTestRule.setContent {
            FinancyTheme {
                IosGlassPillButton(
                    text = "Haptic Test",
                    onClick = { pillClicked = true },
                    modifier = Modifier.testTag("haptic_pill")
                )
            }
        }

        // Verify click invokes HapticUtil and completes gracefully
        composeTestRule.onNodeWithTag("haptic_pill").performClick()
        assertTrue(pillClicked)
    }
}
