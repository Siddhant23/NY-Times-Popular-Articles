package com.test.android.siddhant.ui.navigation

import androidx.navigation3.runtime.NavKey
import com.test.android.siddhant.model.data.ResultsItem
import kotlinx.serialization.Serializable

@Serializable
data object PopularListRoute : NavKey

@Serializable
data class PopularDetailRoute(val article: ResultsItem) : NavKey
