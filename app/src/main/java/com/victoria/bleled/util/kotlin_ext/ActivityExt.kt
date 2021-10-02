package com.victoria.bleled.util.kotlin_ext

import androidx.appcompat.app.AppCompatActivity
import com.victoria.bleled.app.ViewModelFactory
import com.victoria.bleled.data.DataRepository

fun AppCompatActivity.getViewModelFactory(): ViewModelFactory {
    val repository = DataRepository.provideDataRepository(applicationContext)
    return ViewModelFactory(repository, this)
}
