package com.test.android.siddhant.utils

import java.io.IOException

class NoConnectivityException(override var message: String) : IOException()
class UnknownException(override var message: String) : Exception()