package com.test.android.siddhant.model.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PopularArticleResponse(
    @SerialName("status")
    val status: String? = null,
    @SerialName("copyright")
    val copyright: String? = null,
    @SerialName("num_results")
    val numResults: Int? = null,
    @SerialName("results")
    val results: List<ResultsItem>? = null,
)
