package com.test.android.siddhant.model.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class PopularArticleResponse(

    @SerializedName("status")
    val status: String? = null,

    @SerializedName("copyright")
    val copyright: String? = null,

    @SerializedName("num_results")
    val numResults: Int? = null,

    @SerializedName("results")
    val results: ArrayList<ResultsItem>? = null
):Parcelable