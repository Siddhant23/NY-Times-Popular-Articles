package com.test.android.siddhant.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.test.android.siddhant.model.data.ResultsItem
import com.test.android.siddhant.model.repository.PopularRepo

class PopularVM(application: Application) : AndroidViewModel(application) {
    internal var articlesListLiveData = MutableLiveData<ArrayList<ResultsItem>>()

    internal fun fetchArticlesList() {
        articlesListLiveData = PopularRepo(getApplication()).getResponse()
    }
}