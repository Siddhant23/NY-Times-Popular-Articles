package com.test.android.siddhant.model.repository

import com.test.android.siddhant.model.api.ApiBuilder
import com.test.android.siddhant.model.data.ResultsItem
import com.test.android.siddhant.utils.AppConstant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PopularRepo {

    suspend fun getPopularData() : ArrayList<ResultsItem>? {
        return withContext(Dispatchers.IO) {
            ApiBuilder.apiService.getPopularData(AppConstant.KEY).results
        }
    }
}

