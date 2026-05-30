package com.test.android.siddhant.model.api

import com.test.android.siddhant.model.data.PopularArticleResponse
import com.test.android.siddhant.utils.AppConstant
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET(AppConstant.POPULAR)
    suspend fun getPopularData(
        @Query(AppConstant.KEY_API) apiKey: String = com.test.android.siddhant.BuildConfig.API_KEY
    ): PopularArticleResponse
}
