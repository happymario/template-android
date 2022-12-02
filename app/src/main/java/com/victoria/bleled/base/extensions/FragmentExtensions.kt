package com.victoria.bleled.base.extensions

import androidx.fragment.app.Fragment
import com.victoria.bleled.app.ViewModelFactory
import com.victoria.bleled.data.DataRepository

fun Fragment.getViewModelFactory(): ViewModelFactory {
    val repository = DataRepository.provideDataRepository(activity?.applicationContext)
    return ViewModelFactory(repository, this)
}
