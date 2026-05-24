package com.test.android.siddhant.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object PopularListRoute : NavKey

@Serializable
data class PopularDetailRoute(val articleAbstract: String) : NavKey
