package com.test.android.siddhant.utils

import okio.IOException

class NoConnectivityException(override var message: String) : IOException()

class UnknownException(override var message: String?) : IOException()
