package com.mario.template.base

open class BaseViewState(
    open val isLoading: Boolean = false,
    open val error: Throwable? = null,
)
