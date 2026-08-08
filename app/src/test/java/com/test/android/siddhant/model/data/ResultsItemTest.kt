package com.test.android.siddhant.model.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class ResultsItemTest {
    @Test
    fun `listKey uses api id when available`() {
        val article = ResultsItem(id = 42L, title = "Article")

        assertEquals("id:42", article.listKey(index = 3))
    }

    @Test
    fun `listKey remains unique for null id duplicate articles`() {
        val first = ResultsItem(title = "Same", abstract = "Body")
        val second = ResultsItem(title = "Same", abstract = "Body")

        assertNotEquals(first.listKey(index = 0), second.listKey(index = 1))
    }
}
