package com.test.android.siddhant.model.repository

import com.test.android.siddhant.di.ApplicationScope
import com.test.android.siddhant.model.api.ApiService
import com.test.android.siddhant.model.data.ResultsItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PopularRepo @Inject constructor(
    private val apiService: ApiService,
    @ApplicationScope private val ioScope: CoroutineScope
) {

    suspend fun getPopularData(): ArrayList<ResultsItem>? {
        return withContext(ioScope.coroutineContext) {
            apiService.getPopularData().results
        }
    }
}

