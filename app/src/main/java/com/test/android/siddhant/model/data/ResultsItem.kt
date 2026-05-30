package com.test.android.siddhant.model.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
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
) : Parcelable
