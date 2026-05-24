package com.test.android.siddhant.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.test.android.siddhant.di.ApplicationScope
import com.test.android.siddhant.model.data.ResultsItem
import com.test.android.siddhant.model.repository.PopularRepo
import com.test.android.siddhant.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PopularVM
    @Inject
    constructor(
        private val popularRepo: PopularRepo,
        @ApplicationScope private val ioScope: CoroutineScope,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow<Resource<ArrayList<ResultsItem>?>>(Resource.Loading())
        val uiState: StateFlow<Resource<ArrayList<ResultsItem>?>> = _uiState.asStateFlow()

        init {
            fetchArticlesList()
        }

        private fun fetchArticlesList() {
            viewModelScope.launch(ioScope.coroutineContext) {
                _uiState.value = Resource.Loading()
                runCatching { popularRepo.getPopularData() }
                    .onSuccess { result -> _uiState.value = Resource.Success(result) }
                    .onFailure { throwable ->
                        _uiState.value = Resource.Error(throwable.message.orEmpty())
                    }
            }
        }
    }
