package com.test.android.siddhant.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.test.android.siddhant.model.data.ResultsItem
import com.test.android.siddhant.model.repository.PopularRepo
import com.test.android.siddhant.utils.Resource
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PopularVM(private val popularRepo: PopularRepo) : ViewModel() {
    internal var articlesListLiveData = MutableLiveData<Resource<ArrayList<ResultsItem>?>>()

    internal suspend fun fetchArticlesList() {

        articlesListLiveData.apply {
            postValue(Resource.Loading())
        }

        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            articlesListLiveData.apply {
                postValue(
                    exception.message?.let {
                        Resource.Error(it)
                    }
                )
            }
        }

        viewModelScope.launch(Dispatchers.IO +  exceptionHandler) {
            val results = popularRepo.getPopularData()
            articlesListLiveData.apply {
                postValue(Resource.Success(results))
            }
        }
    }
}