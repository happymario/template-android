package com.mario.template.base

import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

abstract class BaseLayoutBindingActivity<T : ViewDataBinding> : BaseLayoutActivity() {
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
    override fun setContentView(layoutResID: Int) {
        binding = DataBindingUtil.inflate(layoutInflater, layoutResID, null, true)
        binding.lifecycleOwner = this
        super.setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()

        // update Language
        binding.invalidateAll()
    }
    /************************************************************
     * Event Handler
     */
    /************************************************************
     * Helpers
     */
    protected open fun initView() {
        binding.lifecycleOwner = this
    }
    /************************************************************
     * Sub Classes
     */
}