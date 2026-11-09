package com.test.android.siddhant.ui.screens

import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.junit4.v2.createComposeRule
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

    @Test
    fun `duplicate or zero ids do not crash list rendering`() {
        val first = ResultsItem(id = 0L, title = "First zero id")
        val second = ResultsItem(id = 0L, title = "Second zero id")
        val third = ResultsItem(id = 42L, title = "Duplicate id 1")
        val fourth = ResultsItem(id = 42L, title = "Duplicate id 2")

        composeRule.setContent {
            NYTimesTheme {
                PopularListScreen(
                    uiState = Resource.Success(listOf(first, second, third, fourth)),
                    onArticleClick = {},
                    onRetry = {},
                )
            }
        }

        composeRule.onNodeWithText("First zero id").assertExists()
        composeRule.onNodeWithText("Second zero id").assertExists()
        composeRule.onNodeWithText("Duplicate id 1").assertExists()
        composeRule.onNodeWithText("Duplicate id 2").assertExists()
    }

    @Test
    fun `error block is a polite live region so TalkBack announces it`() {
        composeRule.setContent {
            NYTimesTheme {
                PopularListScreen(
                    uiState = Resource.Error(R.string.error_internet),
                    onArticleClick = {},
                    onRetry = {},
                )
            }
        }

        composeRule
            .onNodeWithText("Unable to load articles")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.LiveRegion, LiveRegionMode.Polite))
    }
}
