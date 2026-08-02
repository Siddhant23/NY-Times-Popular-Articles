package com.test.android.siddhant.viewmodel

import com.nhaarman.mockitokotlin2.whenever
import com.test.android.siddhant.TestCoroutineRule
import com.test.android.siddhant.model.data.ResultsItem
import com.test.android.siddhant.model.repository.PopularRepo
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
@Config(manifest = Config.NONE)
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
    fun `uiState becomes Error when repo throws`() =
        testCoroutineRule.runBlockingTest {
            // Given
            whenever(repo.getPopularData()).thenThrow(RuntimeException("network down"))

            // When
            val viewModel = PopularVM(repo)
            advanceUntilIdle()

            // Then
            val state = viewModel.uiState.value
            assertTrue("Expected Error, got $state", state is Resource.Error)
            assertEquals("network down", (state as Resource.Error).message)
        }
}
