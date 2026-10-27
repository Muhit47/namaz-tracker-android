package com.example

import com.example.util.AppVersionHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AppVersionHelperTest {

    @Test
    fun `validate AppVersionHelper display strings in English and Bangla`() {
        val englishVersion = AppVersionHelper.getDisplayVersion(isBangla = false)
        val banglaVersion = AppVersionHelper.getDisplayVersion(isBangla = true)

        assertTrue("English version should start with 'v'", englishVersion.startsWith("v"))
        assertTrue("Bangla version should start with 'ভার্সন '", banglaVersion.startsWith("ভার্সন "))
        
        // Assert digits are converted to Bengali digits in Bangla mode
        if (englishVersion.contains("2")) {
            assertTrue("Bangla version should contain '২'", banglaVersion.contains("২"))
        }
        if (englishVersion.contains("3")) {
            assertTrue("Bangla version should contain '৩'", banglaVersion.contains("৩"))
        }
    }
}
