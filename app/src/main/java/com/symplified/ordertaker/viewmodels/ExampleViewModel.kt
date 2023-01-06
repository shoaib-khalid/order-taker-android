package com.symplified.ordertaker.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ExampleViewModel: ViewModel() {
    val currentCategory: MutableLiveData<String> = MutableLiveData()
    fun setCurrentCategory(category: String) {
        currentCategory.value = category
        Log.d("categories", "ExampleViewModel: setCurrentCategory() to ${category}")
    }
}