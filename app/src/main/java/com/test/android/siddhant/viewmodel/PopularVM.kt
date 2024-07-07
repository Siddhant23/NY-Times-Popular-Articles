package com.test.android.siddhant.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.test.android.siddhant.di.ApplicationScope
import com.test.android.siddhant.model.data.ResultsItem
import com.test.android.siddhant.model.repository.PopularRepo
import com.test.android.siddhant.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PopularVM @Inject constructor(
	private val popularRepo: PopularRepo,
	@ApplicationScope private var ioScope: CoroutineScope
) :
	ViewModel() {
	private val _articlesListLiveData = MutableLiveData<Resource<ArrayList<ResultsItem>?>>()
	internal val articlesListLiveData: LiveData<Resource<ArrayList<ResultsItem>?>> =
		_articlesListLiveData

	suspend fun fetchArticlesList() {
		_articlesListLiveData.postValue(Resource.Loading())
		viewModelScope.launch(ioScope.coroutineContext + exceptionHandler) {
			try {
				val result = popularRepo.getPopularData()
				_articlesListLiveData.postValue(Resource.Success(result))
			} catch (e: Exception) {
				_articlesListLiveData.postValue(Resource.Error(e.message.orEmpty()))
			}
		}
	}

	private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
		_articlesListLiveData.apply {
			postValue(
				exception.message?.let {
					Resource.Error(it)
				}
			)
		}
	}
}
