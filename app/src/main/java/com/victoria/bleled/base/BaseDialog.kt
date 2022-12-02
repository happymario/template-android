package com.victoria.bleled.base

import android.app.Dialog
import android.content.Context
import android.os.Bundle

abstract class BaseDialog(windowContext: Context, style: Int) :
    Dialog(windowContext, style) {
    abstract fun onCreateView()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        onCreateView()
    }
}