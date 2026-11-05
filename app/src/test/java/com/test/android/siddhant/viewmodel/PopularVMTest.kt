package com.test.android.siddhant.viewmodel

import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import com.test.android.siddhant.R
import com.test.android.siddhant.TestCoroutineRule
import com.test.android.siddhant.model.data.ResultsItem
import com.test.android.siddhant.model.repository.PopularRepo
import com.test.android.siddhant.utils.InvalidApiKeyException
import com.test.android.siddhant.utils.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [35])
class PopularVMTest {
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @Mock
    private lateinit var repo: PopularRepo

    @Test
    fun `uiState starts in Loading and becomes Success when repo returns data`() =
        testCoroutineRule.runBlockingTest {
            // Given
            val articles = listOf(ResultsItem(id = 1L, title = "Headline"))
            whenever(repo.getPopularData()).thenReturn(articles)

            // When — init{} launches the fetch, which is queued on the test dispatcher
            val viewModel = PopularVM(repo)
            assertTrue(
                "Expected initial Loading, got ${viewModel.uiState.value}",
                viewModel.uiState.value is Resource.Loading,
            )
            advanceUntilIdle()

            // Then
            val state = viewModel.uiState.value
            assertTrue("Expected Success, got $state", state is Resource.Success)
            assertEquals(articles, state.data)
        }

    @Test
    fun `retry re-fetches and recovers from an earlier failure`() =
        testCoroutineRule.runBlockingTest {
            // Given — the initial fetch fails, the next one succeeds.
            val articles = listOf(ResultsItem(id = 7L, title = "Recovered"))
            whenever(repo.getPopularData())
                .thenThrow(RuntimeException("network down"))
                .thenReturn(articles)

            // When — init{} runs the failing fetch...
            val viewModel = PopularVM(repo)
            advanceUntilIdle()
            assertTrue(
                "Expected Error after the first fetch, got ${viewModel.uiState.value}",
                viewModel.uiState.value is Resource.Error,
            )

            // ...and retry() runs a second one.
            viewModel.retry()
            advanceUntilIdle()

            // Then
            val state = viewModel.uiState.value
            assertTrue("Expected Success after retry, got $state", state is Resource.Success)
            assertEquals(articles, state.data)
            verify(repo, times(2)).getPopularData()
        }

    @Test
    fun `uiState becomes Error with generic message when repo throws an unexpected exception`() =
        testCoroutineRule.runBlockingTest {
            // Given
            whenever(repo.getPopularData()).thenThrow(RuntimeException("boom"))

            // When
            val viewModel = PopularVM(repo)
            advanceUntilIdle()

            // Then
            val state = viewModel.uiState.value
            assertTrue("Expected Error, got $state", state is Resource.Error)
            assertEquals(R.string.error_unknown, (state as Resource.Error).messageRes)
        }

    @Test
    fun `uiState maps an invalid API key to the API key message`() =
        testCoroutineRule.runBlockingTest {
            // Given — suspend functions may throw checked exceptions, so stub with doAnswer.
            whenever(repo.getPopularData()).doAnswer { throw InvalidApiKeyException(401) }

            // When
            val viewModel = PopularVM(repo)
            advanceUntilIdle()

            // Then
            val state = viewModel.uiState.value
            assertTrue("Expected Error, got $state", state is Resource.Error)
            assertEquals(R.string.error_api_key, (state as Resource.Error).messageRes)
        }
}
