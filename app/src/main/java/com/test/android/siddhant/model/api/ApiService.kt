package com.test.android.siddhant.model.api

import com.test.android.siddhant.model.data.PopularArticleResponse
import com.test.android.siddhant.utils.AppConstant
import retrofit2.http.GET

interface ApiService {

	@GET(AppConstant.POPULAR)
	suspend fun getPopularData(): PopularArticleResponse
}
