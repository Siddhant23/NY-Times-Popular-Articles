package com.test.android.siddhant.model.api

import com.test.android.siddhant.utils.HttpStatusException
import com.test.android.siddhant.utils.InvalidApiKeyException
import com.test.android.siddhant.utils.NoConnectivityException
import com.test.android.siddhant.utils.RateLimitedException
import com.test.android.siddhant.utils.ServerException
import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CustomInterceptorTest {
    private val server = MockWebServer()
    private var online = true
    private var bodyClosed = false

    /** Network interceptor that records whether CustomInterceptor closed the response body. */
    private val closeTracker =
        Interceptor { chain ->
            val response = chain.proceed(chain.request())
            val original = response.body
            response
                .newBuilder()
                .body(
                    object : ResponseBody() {
                        override fun contentType() = original.contentType()

                        override fun contentLength() = original.contentLength()

                        override fun source() = original.source()

                        override fun close() {
                            bodyClosed = true
                            original.close()
                        }
                    },
                ).build()
        }

    private val client =
        OkHttpClient
            .Builder()
            .addInterceptor(CustomInterceptor(isOnline = { online }, apiKey = API_KEY))
            .addNetworkInterceptor(closeTracker)
            .build()

    @Before
    fun setUp() {
        server.start()
    }

    @After
    fun tearDown() {
        server.close()
    }

    private fun get() = client.newCall(Request.Builder().url(server.url("/svc/popular.json?x=1")).build()).execute()

    @Test
    fun `adds api-key query parameter and keeps existing ones`() {
        server.enqueue(MockResponse(code = 200, body = "{}"))

        get().use { assertEquals(200, it.code) }

        val recorded = server.takeRequest()
        assertEquals(API_KEY, recorded.url.queryParameter("api-key"))
        assertEquals("1", recorded.url.queryParameter("x"))
    }

    @Test
    fun `offline fails fast without hitting the network`() {
        online = false

        assertThrows(NoConnectivityException::class.java) { get() }
        assertEquals(0, server.requestCount)
    }

    @Test
    fun `401 and 403 map to InvalidApiKeyException and close the response`() {
        for (code in listOf(401, 403)) {
            bodyClosed = false
            server.enqueue(MockResponse(code = code, body = "{\"fault\":\"bad key\"}"))

            val error = assertThrows(InvalidApiKeyException::class.java) { get() }

            assertEquals(code, error.code)
            assertTrue("response for $code should be closed", bodyClosed)
        }
    }

    @Test
    fun `429 maps to RateLimitedException and closes the response`() {
        server.enqueue(MockResponse(code = 429))

        assertThrows(RateLimitedException::class.java) { get() }
        assertTrue(bodyClosed)
    }

    @Test
    fun `5xx maps to ServerException with the status code`() {
        server.enqueue(MockResponse(code = 503))

        val error = assertThrows(ServerException::class.java) { get() }

        assertEquals(503, error.code)
        assertTrue(bodyClosed)
    }

    @Test
    fun `other non-2xx statuses map to HttpStatusException`() {
        server.enqueue(MockResponse(code = 404))

        val error = assertThrows(HttpStatusException::class.java) { get() }

        assertEquals(404, error.code)
        assertTrue(bodyClosed)
    }

    private companion object {
        const val API_KEY = "test-key-123"
    }
}
