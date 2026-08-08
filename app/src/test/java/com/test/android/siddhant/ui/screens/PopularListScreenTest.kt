package com.test.android.siddhant.ui.screens

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.test.android.siddhant.model.data.ResultsItem
import com.test.android.siddhant.ui.theme.NYTimesTheme
import com.test.android.siddhant.utils.Resource
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class PopularListScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `clicking article emits selected item`() {
        val first = ResultsItem(title = "First", abstract = "First body")
        val second = ResultsItem(title = "Second", abstract = "Second body")
        var selected: ResultsItem? = null

        composeRule.setContent {
            NYTimesTheme {
                PopularListScreen(
                    uiState = Resource.Success(listOf(first, second)),
                    onArticleClick = { selected = it },
                    onRetry = {},
                )
            }
        }

        composeRule.onNodeWithText("Second").performClick()

        assertEquals(second, selected)
    }

    @Test
    fun `error state shows retry action`() {
        var retried = false

        composeRule.setContent {
            NYTimesTheme {
                PopularListScreen(
                    uiState = Resource.Error("network down"),
                    onArticleClick = {},
                    onRetry = { retried = true },
                )
            }
        }

        composeRule.onNodeWithText("Unable to load articles").assertExists()
        composeRule.onNodeWithText("Retry").performClick()

        assertTrue(retried)
    }
}
