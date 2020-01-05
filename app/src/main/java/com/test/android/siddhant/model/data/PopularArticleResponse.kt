package com.test.android.siddhant.model.data

import com.google.gson.annotations.SerializedName

data class PopularArticleResponse(

    @SerializedName("status")
    val status: String? = null,

    @SerializedName("num_results")
    val numResults: Int? = null,

    @SerializedName("results")
    val results: ArrayList<ResultsItem>? = null
)