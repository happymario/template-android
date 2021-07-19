package com.victoria.bleled.app

import androidx.fragment.app.Fragment

fun Fragment.getViewModelFactory(): ViewModelFactory {
    val repository = (requireContext().applicationContext as MyApplication).dataRepository
    return ViewModelFactory(repository, this)
}
