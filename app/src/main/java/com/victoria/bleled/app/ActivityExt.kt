package com.victoria.bleled.app

import androidx.appcompat.app.AppCompatActivity

fun AppCompatActivity.getViewModelFactory(): ViewModelFactory {
    val repository = (applicationContext as MyApplication).dataRepository
    return ViewModelFactory(repository, this)
}
