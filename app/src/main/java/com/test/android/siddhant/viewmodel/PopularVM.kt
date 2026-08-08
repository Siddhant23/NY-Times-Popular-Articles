package com.test.android.siddhant.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.test.android.siddhant.model.data.ResultsItem
import com.test.android.siddhant.model.repository.PopularRepo
import com.test.android.siddhant.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
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
    ) : ViewModel() {
        private val _uiState = MutableStateFlow<Resource<List<ResultsItem>>>(Resource.Loading())
        val uiState: StateFlow<Resource<List<ResultsItem>>> = _uiState.asStateFlow()

        init {
            fetchArticlesList()
        }

        /** Re-runs the fetch after a failure. */
        fun retry() {
            fetchArticlesList()
        }

        private fun fetchArticlesList() {
            viewModelScope.launch {
                _uiState.value = Resource.Loading()
                runCatching { popularRepo.getPopularData() }
                    .onSuccess { result -> _uiState.value = Resource.Success(result) }
                    .onFailure { throwable ->
                        _uiState.value = Resource.Error(throwable.message.orEmpty())
                    }
            }
        }
    }
