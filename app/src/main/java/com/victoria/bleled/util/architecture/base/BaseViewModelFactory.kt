package com.victoria.bleled.util.architecture.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

// ViewModelProviders로부터 lifecycle을 받자면 이 facotry로부터 생성된 ViewModel이어야 한다.
class BaseViewModelFactory<T>(val creator: () -> T) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return creator() as T
    }
}
