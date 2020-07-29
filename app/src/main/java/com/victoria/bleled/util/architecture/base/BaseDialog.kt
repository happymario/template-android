package com.victoria.bleled.util.architecture.base


import android.app.Dialog
import android.content.Context
import com.victoria.bleled.R


abstract class BaseDialog : Dialog {

    abstract fun initUI()

    constructor(context: Context) : super(context, R.style.DialogCustomTheme) {
        initUI()
    }
}
