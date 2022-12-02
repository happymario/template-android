package com.victoria.bleled.base

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.databinding.ViewDataBinding
import com.victoria.bleled.base.internal.Inflate

open class BaseBindingDialog<VB : ViewDataBinding>(
    windowContext: Context,
    private val inflate: Inflate<VB>,
) : Dialog(windowContext) {

    protected var binding: VB

    init {
        binding = inflate.invoke(layoutInflater, null, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)
    }
}