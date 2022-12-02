package com.victoria.bleled.base.extensions

import androidx.fragment.app.Fragment
import com.victoria.bleled.app.ViewModelFactory
import com.victoria.bleled.data.net.repository.MyTemplateRepository

fun Fragment.getViewModelFactory(): ViewModelFactory {
    val repository = MyTemplateRepository.provideDataRepository()
    return ViewModelFactory(repository, this)
}
