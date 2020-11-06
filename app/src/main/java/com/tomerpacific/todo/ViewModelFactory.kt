package com.tomerpacific.todo

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tomerpacific.todo.viewmodels.MainActivityViewModel
import java.lang.IllegalArgumentException

class ViewModelFactory(val application: Application) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainActivityViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainActivityViewModel(application) as T
        }
        throw IllegalArgumentException("Unable to create viewmodel")
    }
}