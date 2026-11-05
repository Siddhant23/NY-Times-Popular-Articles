package com.test.android.siddhant.viewmodel

import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import com.test.android.siddhant.TestCoroutineRule
import com.test.android.siddhant.model.data.ResultsItem
import com.test.android.siddhant.model.repository.PopularRepo
import com.test.android.siddhant.utils.NoConnectivityException
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
    fun `uiState becomes Error with the typed network message when repo throws NoConnectivityException`() =
        testCoroutineRule.runBlockingTest {
            // Given (thenAnswer: Mockito rejects checked exceptions in thenThrow for suspend funs)
            whenever(repo.getPopularData()).thenAnswer { throw NoConnectivityException("Please check your internet connection") }

            // When
            val viewModel = PopularVM(repo)
            advanceUntilIdle()

            // Then
            val state = viewModel.uiState.value
            assertTrue("Expected Error, got $state", state is Resource.Error)
            assertEquals("Please check your internet connection", (state as Resource.Error).message)
        }

    @Test
    fun `untyped exception message is not shown to the user`() =
        testCoroutineRule.runBlockingTest {
            // Given — e.g. a JSON parse error from a captive-portal HTML page.
            whenever(repo.getPopularData()).thenThrow(IllegalStateException("Unexpected JSON token: <html>"))

            // When
            val viewModel = PopularVM(repo)
            advanceUntilIdle()

            // Then — blank message makes the UI fall back to its generic error text.
            val state = viewModel.uiState.value
            assertTrue("Expected Error, got $state", state is Resource.Error)
            assertEquals("", (state as Resource.Error).message)
        }

    @Test
    fun `retry while a fetch is in flight does not start a second request`() =
        testCoroutineRule.runBlockingTest {
            // Given — the init{} fetch is queued but has not completed yet.
            whenever(repo.getPopularData()).thenReturn(emptyList())
            val viewModel = PopularVM(repo)

            // When — the user taps Retry twice before the first fetch finishes.
            viewModel.retry()
            viewModel.retry()
            advanceUntilIdle()

            // Then
            verify(repo, times(1)).getPopularData()
            assertTrue(viewModel.uiState.value is Resource.Success)
        }
}
