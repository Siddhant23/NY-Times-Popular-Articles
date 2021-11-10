package com.test.android.siddhant.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.test.android.siddhant.model.repository.PopularRepo

@Suppress("UNCHECKED_CAST")
class ViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PopularVM::class.java)) {
            return PopularVM(PopularRepo()) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}