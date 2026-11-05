package com.test.android.siddhant.utils

import androidx.annotation.StringRes

sealed class Resource<T>(val data: T? = null) {
    class Success<T>(data: T) : Resource<T>(data)

    /** [messageRes] is a user-facing string resource; see [toMessageRes]. */
    class Error<T>(@StringRes val messageRes: Int, data: T? = null) : Resource<T>(data)

    class Loading<T> : Resource<T>()
}
