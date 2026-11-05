package com.test.android.siddhant.utils

import com.test.android.siddhant.R
import org.junit.Assert.assertEquals
import org.junit.Test
import java.net.SocketTimeoutException

class ExceptionsTest {
    @Test
    fun `maps each failure to its user-facing message`() {
        assertEquals(R.string.error_internet, NoConnectivityException().toMessageRes())
        assertEquals(R.string.error_internet, SocketTimeoutException().toMessageRes())
        assertEquals(R.string.error_api_key, InvalidApiKeyException(401).toMessageRes())
        assertEquals(R.string.error_rate_limited, RateLimitedException().toMessageRes())
        assertEquals(R.string.error_server, ServerException(503).toMessageRes())
        assertEquals(R.string.error_unknown, HttpStatusException(404).toMessageRes())
        assertEquals(R.string.error_unknown, IllegalStateException().toMessageRes())
    }
}
