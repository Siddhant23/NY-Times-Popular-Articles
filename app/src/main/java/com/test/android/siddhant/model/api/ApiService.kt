package com.test.android.siddhant.model.api

import com.test.android.siddhant.model.data.PopularArticleResponse
import com.test.android.siddhant.utils.AppConstant
import retrofit2.http.GET

interface ApiService {
    // The api-key query param is added centrally by CustomInterceptor.
    @GET(AppConstant.POPULAR)
    suspend fun getPopularData(): PopularArticleResponse
}
