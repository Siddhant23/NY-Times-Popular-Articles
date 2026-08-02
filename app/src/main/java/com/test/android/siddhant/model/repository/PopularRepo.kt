package com.test.android.siddhant.model.repository

import com.test.android.siddhant.di.IoDispatcher
import com.test.android.siddhant.model.api.ApiService
import com.test.android.siddhant.model.data.ResultsItem
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PopularRepo
    @Inject
    constructor(
        private val apiService: ApiService,
        @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    ) {
        suspend fun getPopularData(): List<ResultsItem> =
            withContext(ioDispatcher) {
                apiService.getPopularData().results.orEmpty()
            }
    }
