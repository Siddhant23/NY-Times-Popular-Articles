package com.test.android.siddhant.model.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ResultsItem(
    @SerializedName("id")
    val id: Long? = null,
    @SerializedName("byline")
    val byline: String? = null,
    @SerializedName("title")
    val title: String? = null,
    @SerializedName("abstract")
    val abstract: String? = null,
    @SerializedName("published_date")
    val publishedDate: String? = null,
) : Parcelable
