package com.victoria.bleled.util.architecture.base


import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

abstract class BindingActivity<T : ViewDataBinding> : BaseActivity() {
    /************************************************************
     * Static & Global Members
     ************************************************************/


    /************************************************************
     * UI controls & Data members
     **********************************************************/
    lateinit var binding: T


    /************************************************************
     * Overrides
     ************************************************************/
    abstract fun getLayout(): Int

    abstract fun initUI()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.binding = DataBindingUtil.setContentView(this, getLayout())

        initUI()
    }


    /************************************************************
     * Event Handler
     ************************************************************/


    /************************************************************
     * Helpers
     ************************************************************/


    /************************************************************
     * Sub Classes
     ************************************************************/

}
