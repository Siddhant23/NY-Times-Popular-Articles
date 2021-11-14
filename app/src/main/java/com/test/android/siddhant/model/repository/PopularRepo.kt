package com.test.android.siddhant.model.repository

import com.test.android.siddhant.model.api.ApiService
import com.test.android.siddhant.model.data.ResultsItem
import com.test.android.siddhant.utils.AppConstant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PopularRepo @Inject constructor(private val apiService: ApiService) {

    suspend fun getPopularData() : ArrayList<ResultsItem>? {
        return withContext(Dispatchers.IO) {
            apiService.getPopularData(AppConstant.KEY).results
        }
    }
}

