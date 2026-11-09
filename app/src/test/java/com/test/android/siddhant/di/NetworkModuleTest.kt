package com.test.android.siddhant.di

import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import okhttp3.OkHttpClient
import okhttp3.Request
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class NetworkModuleTest {
    private val server = MockWebServer()
    private val logs = mutableListOf<String>()

    @Before
    fun setUp() {
        server.start()
    }

    @After
    fun tearDown() {
        server.close()
    }

    @Test
    fun `debug logging interceptor redacts the api-key query parameter`() {
        server.enqueue(MockResponse(code = 200, body = "{}"))
        val client =
            OkHttpClient
                .Builder()
                .addInterceptor(NetworkModule.debugLoggingInterceptor { logs += it })
                .build()

        client.newCall(Request.Builder().url(server.url("/svc/popular.json?api-key=$SECRET&period=7")).build()).execute().close()

        val output = logs.joinToString("\n")
        assertTrue("expected the request URL to be logged:\n$output", output.contains("period=7"))
        assertFalse("api key leaked into logs:\n$output", output.contains(SECRET))
    }

    private companion object {
        const val SECRET = "super-secret-nyt-key"
    }
}
