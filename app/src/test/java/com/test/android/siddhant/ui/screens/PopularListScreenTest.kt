package com.test.android.siddhant.ui.screens

import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.text.AnnotatedString
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

    @Test
    fun `error state shows the error cause inline`() {
        composeRule.setContent {
            NYTimesTheme {
                PopularListScreen(
                    uiState = Resource.Error("Too many requests. Please try again in a minute."),
                    onArticleClick = {},
                    onRetry = {},
                )
            }
        }

        composeRule.onNodeWithText("Too many requests. Please try again in a minute.").assertExists()
    }

    @Test
    fun `error block is a polite live region and blank cause shows only generic text`() {
        composeRule.setContent {
            NYTimesTheme {
                PopularListScreen(
                    uiState = Resource.Error(""),
                    onArticleClick = {},
                    onRetry = {},
                )
            }
        }

        composeRule
            .onNodeWithText("Unable to load articles")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.LiveRegion, LiveRegionMode.Polite))
        // Only the generic title (no empty second text line) is merged into the error node.
        composeRule
            .onNodeWithText("Unable to load articles")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Text, listOf(AnnotatedString("Unable to load articles"))))
    }
}
