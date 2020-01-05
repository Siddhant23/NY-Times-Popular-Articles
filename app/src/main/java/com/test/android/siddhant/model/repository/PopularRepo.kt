package com.test.android.siddhant.model.repository

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.test.android.siddhant.model.api.ApiBuilder
import com.test.android.siddhant.model.data.PopularArticleResponse
import com.test.android.siddhant.model.data.ResultsItem
import com.test.android.siddhant.utils.AppConstant
import com.test.android.siddhant.utils.Util
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PopularRepo(var application: Application) {

    private var responseLiveData = MutableLiveData<ArrayList<ResultsItem>>()

    internal fun getResponse(): MutableLiveData<ArrayList<ResultsItem>> {

        return when {
            Util.isNetworkAvailable(application) -> {
                fetchfromRemote()
            }
            else -> {
                sendInternetError()
            }
        }
    }

    private fun sendInternetError(): MutableLiveData<ArrayList<ResultsItem>> {
        responseLiveData.value = null
        Toast.makeText(application, AppConstant.ERR_INTERNET_MSG, Toast.LENGTH_LONG).show()
        return responseLiveData
    }


    private fun fetchfromRemote(): MutableLiveData<ArrayList<ResultsItem>> {
        val call: Call<PopularArticleResponse> =
            ApiBuilder.apiService.getPopularData(AppConstant.KEY)
        call.enqueue(object : Callback<PopularArticleResponse> {

            override fun onResponse(
                call: Call<PopularArticleResponse>,
                response: Response<PopularArticleResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val articleResponse = response.body()
                    responseLiveData.value = (articleResponse?.results)
                } else {
                    responseLiveData.value = null
                    Toast.makeText(application, AppConstant.ERR_PARSING, Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<PopularArticleResponse>, t: Throwable) {
                responseLiveData.value = null
                Toast.makeText(application, t.message, Toast.LENGTH_LONG).show()
            }
        })
        return responseLiveData
    }

}

