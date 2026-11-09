package com.test.android.siddhant.ui.screens

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.test.android.siddhant.R
import com.test.android.siddhant.model.data.ResultsItem
import com.test.android.siddhant.ui.theme.NYTimesTheme
import com.test.android.siddhant.utils.Resource
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class PopularListScreenAndroidTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun clickingArticleEmitsSelectedItem() {
        val first = ResultsItem(title = "Instrumented first", abstract = "First body")
        val second = ResultsItem(title = "Instrumented second", abstract = "Second body")
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

        composeRule.onNodeWithText("Instrumented second").performClick()

        assertEquals(second, selected)
    }

    @Test
    fun errorStateShowsRetryAction() {
        var retried = false

        composeRule.setContent {
            NYTimesTheme {
                PopularListScreen(
                    uiState = Resource.Error(R.string.error_internet),
                    onArticleClick = {},
                    onRetry = { retried = true },
                )
            }
        }

        composeRule.onNodeWithText("Unable to load articles").assertExists()
        composeRule.onNodeWithText("Please check your internet connection").assertExists()
        composeRule.onNodeWithText("Retry").performClick()

        assertTrue(retried)
    }
}
