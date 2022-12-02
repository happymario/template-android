package com.victoria.bleled.base

import android.os.Bundle
import androidx.databinding.ViewDataBinding


abstract class BaseBindingVMActivity<VB : ViewDataBinding, VM : BaseViewModel> :
    BaseBindingActivity<VB>() {
    protected abstract val viewModel: VM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        observeViewModel()
        observeViewEvent()
    }

    /**
     * ViewModel 이벤트 처리
     */
    open fun observeViewModel() {}

    /**
     * View 의 직접적인 이벤트 처리
     */
    open fun observeViewEvent() {}
}