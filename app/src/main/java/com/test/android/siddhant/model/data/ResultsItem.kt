package com.test.android.siddhant.model.data

import com.google.gson.annotations.SerializedName

data class ResultsItem(

    @SerializedName("id")
    val id: Long? = null,

    @SerializedName("byline")
    val byline: String? = null,

    @SerializedName("abstract")
    val abstract: String? = null,

    @SerializedName("published_date")
    val publishedDate: String? = null
)