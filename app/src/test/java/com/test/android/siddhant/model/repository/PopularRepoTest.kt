package com.test.android.siddhant.model.repository

import com.test.android.siddhant.model.api.ApiService
import com.test.android.siddhant.model.data.PopularArticleResponse
import com.test.android.siddhant.model.data.ResultsItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.CoroutineContext

@OptIn(ExperimentalCoroutinesApi::class)
class PopularRepoTest {
    private class FakeApiService(
        private val response: PopularArticleResponse,
    ) : ApiService {
        var callContext: CoroutineContext? = null

        override suspend fun getPopularData(): PopularArticleResponse {
            callContext = currentCoroutineContext()
            return response
        }
    }

    @Test
    fun `returns the results list`() =
        runTest {
            val articles = listOf(ResultsItem(id = 1L, title = "A"), ResultsItem(id = 2L, title = "B"))
            val repo = PopularRepo(FakeApiService(PopularArticleResponse(results = articles)), StandardTestDispatcher(testScheduler))

            assertEquals(articles, repo.getPopularData())
        }

    @Test
    fun `null results become an empty list`() =
        runTest {
            val repo = PopularRepo(FakeApiService(PopularArticleResponse(results = null)), StandardTestDispatcher(testScheduler))

            assertTrue(repo.getPopularData().isEmpty())
        }

    @Test
    fun `api call runs on the injected IO dispatcher`() =
        runTest {
            val ioDispatcher = StandardTestDispatcher(testScheduler)
            val api = FakeApiService(PopularArticleResponse(results = emptyList()))

            PopularRepo(api, ioDispatcher).getPopularData()

            assertSame(ioDispatcher, api.callContext?.get(ContinuationInterceptor))
        }
}
