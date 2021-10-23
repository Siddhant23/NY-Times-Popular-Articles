package com.test.android.siddhant.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.test.android.siddhant.model.repository.PopularRepo

class ViewModelFactory(private val repo: PopularRepo) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PopularVM::class.java)) {
            return PopularVM(repo) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}