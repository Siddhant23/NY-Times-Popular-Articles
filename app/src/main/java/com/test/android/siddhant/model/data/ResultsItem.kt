package com.test.android.siddhant.model.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResultsItem(
    @SerialName("id")
    val id: Long? = null,
    @SerialName("byline")
    val byline: String? = null,
    @SerialName("title")
    val title: String? = null,
    @SerialName("abstract")
    val abstract: String? = null,
    @SerialName("published_date")
    val publishedDate: String? = null,
)

fun ResultsItem.listKey(index: Int): String = id?.let { "id:$it" } ?: "position:$index"
