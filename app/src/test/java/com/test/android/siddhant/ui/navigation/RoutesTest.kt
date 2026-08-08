package com.test.android.siddhant.ui.navigation

import com.test.android.siddhant.model.data.ResultsItem
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test

class RoutesTest {
    @Test
    fun `detail route carries article snapshot`() {
        val article =
            ResultsItem(
                id = 12L,
                byline = "By Reporter",
                title = "Saved article",
                abstract = "Saved abstract",
                publishedDate = "2026-08-09",
            )

        val encoded = Json.encodeToString(PopularDetailRoute.serializer(), PopularDetailRoute(article))
        val decoded = Json.decodeFromString(PopularDetailRoute.serializer(), encoded)

        assertEquals(article, decoded.article)
    }
}
