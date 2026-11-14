package com.numbergarden.app

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test

/**
 * Smoke test for the home screen. Run on a device or emulator:
 * ./gradlew :app:connectedDebugAndroidTest
 */
class HomeSmokeTest {

    @get:Rule
    val rule = createAndroidComposeRule<MainActivity>()

    @Test
    fun homeShowsTheFourLearningChoices() {
        rule.onNodeWithText("Let's play.").assertIsDisplayed()
        rule.onNodeWithText("Watch").assertIsDisplayed()
        rule.onNodeWithText("Count").assertIsDisplayed()
        rule.onNodeWithText("Compare").assertIsDisplayed()
        rule.onNodeWithText("Numbers").assertIsDisplayed()
    }
}
