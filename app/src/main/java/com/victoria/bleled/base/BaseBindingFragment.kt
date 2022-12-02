package com.victoria.bleled.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

abstract class BaseBindingFragment<T : ViewDataBinding> : BaseFragment() {
    /************************************************************
     * Static & Global Members
     */
    /************************************************************
     * UI controls & Data members
     */
    lateinit var binding: T

    /************************************************************
     * Overrides
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        root = binding.root
        binding.lifecycleOwner = this
        return root
    }

    override fun onResume() {
        super.onResume()

        // updateLang
        binding.invalidateAll()
    }

    override fun onDestroy() {
        // binding.unbind()
        super.onDestroy()
    }

    /************************************************************
     * Event Handler
     */
    /************************************************************
     * Helpers
     */
}