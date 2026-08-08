package com.test.android.siddhant.ui.screens

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.test.android.siddhant.model.data.ResultsItem
import com.test.android.siddhant.ui.theme.NYTimesTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class PopularDetailScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `renders article from route snapshot`() {
        composeRule.setContent {
            NYTimesTheme {
                PopularDetailScreen(
                    article =
                        ResultsItem(
                            title = "Route title",
                            byline = "By Route Reporter",
                            abstract = "Route abstract",
                            publishedDate = "2026-08-09",
                        ),
                    onBack = {},
                )
            }
        }

        composeRule.onNodeWithText("Route title").assertExists()
        composeRule.onNodeWithText("By Route Reporter").assertExists()
        composeRule.onNodeWithText("Route abstract").assertExists()
        composeRule.onNodeWithText("2026-08-09").assertExists()
    }

    @Test
    fun `back button invokes callback`() {
        var clicked = false
        composeRule.setContent {
            NYTimesTheme {
                PopularDetailScreen(
                    article = ResultsItem(title = "Title"),
                    onBack = { clicked = true },
                )
            }
        }

        composeRule.onNodeWithContentDescription("Back").performClick()

        assertTrue(clicked)
    }
}
