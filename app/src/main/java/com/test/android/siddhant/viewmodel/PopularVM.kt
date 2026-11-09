package com.test.android.siddhant.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.test.android.siddhant.model.data.ResultsItem
import com.test.android.siddhant.model.repository.PopularRepo
import com.test.android.siddhant.utils.Resource
import com.test.android.siddhant.utils.toMessageRes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
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

        // Must be declared before init{}: property initializers run in declaration order.
        private var fetchJob: Job? = null

        init {
            fetchArticlesList()
        }

        /** Re-runs the fetch after a failure. Ignored while a fetch is already running. */
        fun retry() {
            if (fetchJob?.isActive == true) return
            fetchArticlesList()
        }

        private fun fetchArticlesList() {
            fetchJob = viewModelScope.launch {
                _uiState.value = Resource.Loading()
                _uiState.value =
                    try {
                        Resource.Success(popularRepo.getPopularData())
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: Exception) {
                        Resource.Error(e.toMessageRes())
                    }
            }
        }
    }
