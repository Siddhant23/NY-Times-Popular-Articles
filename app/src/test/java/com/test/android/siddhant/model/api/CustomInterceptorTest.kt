package com.test.android.siddhant.model.api

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.test.core.app.ApplicationProvider
import com.test.android.siddhant.R
import com.test.android.siddhant.di.NetworkModule
import com.test.android.siddhant.utils.ApiException
import com.test.android.siddhant.utils.AppConstant
import com.test.android.siddhant.utils.NoConnectivityException
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import okhttp3.OkHttpClient
import okhttp3.Request
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowNetworkCapabilities
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException


@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class CustomInterceptorTest {
    private val context: Context = ApplicationProvider.getApplicationContext()
    private val server = MockWebServer()
    private lateinit var client: OkHttpClient

    @Before
    fun setUp() {
        server.start()
        setOnline(true)
        client = OkHttpClient.Builder().addInterceptor(CustomInterceptor(context)).build()
    }

    @After
    fun tearDown() {
        server.close()
    }

    @Test
    fun `success passes response through and appends api-key query parameter`() {
        server.enqueue(MockResponse.Builder().code(200).body("ok").build())

        val body = execute().use { it.body.string() }

        assertEquals("ok", body)
        val recorded = server.takeRequest()
        assertTrue(
            "api-key query parameter missing",
            AppConstant.KEY_API in recorded.url.queryParameterNames,
        )
    }

    @Test
    fun `401 maps to ApiException with unauthorized message`() {
        assertApiError(401, context.getString(R.string.error_unauthorized))
    }

    @Test
    fun `429 maps to ApiException with rate-limit message`() {
        assertApiError(429, context.getString(R.string.error_rate_limited))
    }

    @Test
    fun `5xx maps to ApiException with server message`() {
        assertApiError(503, context.getString(R.string.error_server))
    }

    @Test
    fun `other non-2xx maps to ApiException with HTTP code in message`() {
        assertApiError(404, context.getString(R.string.error_http, 404))
    }

    @Test
    fun `offline throws NoConnectivityException without hitting the network`() {
        setOnline(false)

        try {
            execute()
            fail("Expected NoConnectivityException")
        } catch (e: NoConnectivityException) {
            assertEquals(context.getString(R.string.error_internet), e.message)
        }
        assertEquals(0, server.requestCount)
    }

    @Test
    fun `VPN-only network with internet capability is treated as online`() {
        setNetwork(transport = NetworkCapabilities.TRANSPORT_VPN, hasInternet = true)
        server.enqueue(MockResponse.Builder().code(200).body("ok").build())

        val body = execute().use { it.body.string() }

        assertEquals("ok", body)
    }

    @Test
    fun `wifi without internet capability is treated as offline`() {
        setNetwork(transport = NetworkCapabilities.TRANSPORT_WIFI, hasInternet = false)

        try {
            execute()
            fail("Expected NoConnectivityException")
        } catch (e: NoConnectivityException) {
            assertEquals(0, server.requestCount)
        }
    }

    @Test
    fun `DNS failure, refused connection and timeout map to NoConnectivityException`() {
        listOf(
            UnknownHostException("api.nytimes.com"),
            ConnectException("Failed to connect"),
            SocketTimeoutException("timeout"),
        ).forEach { failure ->
            // A downstream interceptor stands in for the network layer failing.
            val failingClient =
                OkHttpClient.Builder()
                    .addInterceptor(CustomInterceptor(context))
                    .addInterceptor { throw failure }
                    .build()

            try {
                failingClient.newCall(Request.Builder().url(server.url("/svc/test.json")).build()).execute()
                fail("Expected NoConnectivityException for ${failure::class.simpleName}")
            } catch (e: NoConnectivityException) {
                assertEquals(context.getString(R.string.error_internet), e.message)
                assertSame(failure, e.cause)
            }
        }
    }

    @Test
    fun `retrofit converter parses popular response and ignores unknown keys`() =
        runTest {
            server.enqueue(
                MockResponse.Builder()
                    .code(200)
                    .body(
                        """
                        {"status":"OK","num_results":1,"unknown_field":true,
                         "results":[{"id":7,"title":"Headline","byline":"By A","abstract":"Body",
                                     "published_date":"2026-11-01","media":[]}]}
                        """.trimIndent(),
                    ).build(),
            )
            val api =
                Retrofit.Builder()
                    .baseUrl(server.url("/svc/"))
                    .client(client)
                    .addConverterFactory(
                        NetworkModule.providesJson().asConverterFactory("application/json".toMediaType()),
                    ).build()
                    .create(ApiService::class.java)

            val response = api.getPopularData()

            assertEquals(1, response.results?.size)
            assertEquals("Headline", response.results?.first()?.title)
            assertEquals(7L, response.results?.first()?.id)
        }

    private fun execute() = client.newCall(Request.Builder().url(server.url("/svc/test.json")).build()).execute()

    private fun assertApiError(
        code: Int,
        expectedMessage: String,
    ) {
        server.enqueue(MockResponse.Builder().code(code).body("error body").build())
        try {
            execute()
            fail("Expected ApiException for HTTP $code")
        } catch (e: ApiException) {
            assertEquals(code, e.code)
            assertEquals(expectedMessage, e.message)
        }
    }

    private fun setOnline(online: Boolean) {
        if (online) {
            setNetwork(transport = NetworkCapabilities.TRANSPORT_WIFI, hasInternet = true)
        } else {
            setNetwork(transport = null, hasInternet = false)
        }
    }

    private fun setNetwork(
        transport: Int?,
        hasInternet: Boolean,
    ) {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = ShadowNetworkCapabilities.newInstance()
        transport?.let { shadowOf(capabilities).addTransportType(it) }
        if (hasInternet) shadowOf(capabilities).addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        shadowOf(cm).setNetworkCapabilities(cm.activeNetwork, capabilities)
    }
}
