package com.victoria.bleled.util.architecture.base


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment

/**
 * @author Leopold
 */
abstract class BindingFragment<T : ViewDataBinding> : Fragment() {
    @LayoutRes
    abstract fun getLayoutResId(): Int

    lateinit var binding: T

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return DataBindingUtil.inflate<T>(inflater, getLayoutResId(), container, false)
            .apply { binding = this }.root
    }
}