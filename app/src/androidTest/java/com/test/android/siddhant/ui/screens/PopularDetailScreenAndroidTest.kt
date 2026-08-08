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

class PopularDetailScreenAndroidTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun rendersArticleFromRouteSnapshot() {
        composeRule.setContent {
            NYTimesTheme {
                PopularDetailScreen(
                    article =
                        ResultsItem(
                            title = "Instrumented route title",
                            byline = "By Instrumented Reporter",
                            abstract = "Instrumented route abstract",
                            publishedDate = "2026-08-09",
                        ),
                    onBack = {},
                )
            }
        }

        composeRule.onNodeWithText("Instrumented route title").assertExists()
        composeRule.onNodeWithText("By Instrumented Reporter").assertExists()
        composeRule.onNodeWithText("Instrumented route abstract").assertExists()
        composeRule.onNodeWithText("2026-08-09").assertExists()
    }

    @Test
    fun backButtonInvokesCallback() {
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
