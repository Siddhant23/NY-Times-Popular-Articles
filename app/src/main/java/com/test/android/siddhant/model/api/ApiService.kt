package com.test.android.siddhant.model.api

import com.test.android.siddhant.model.data.PopularArticleResponse
import com.test.android.siddhant.utils.AppConstant
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET(AppConstant.POPULAR_7)
    fun getPopularData(@Query(AppConstant.API_KEY) apiKey: String): Call<PopularArticleResponse>
}