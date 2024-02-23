package com.mario.lib.base.architecture

import androidx.lifecycle.MediatorLiveData

abstract class LiveValidator {
    abstract val validators: List<LiveDataValidator>

    val isValidMediator = MediatorLiveData<Boolean>()

    var valid: Boolean = false
        private set
        get() = isValidMediator.value ?: false

    val errors by lazy {
        MediatorLiveData<String?>().apply {
            for (validator in validators) {
                addSource(validator.error) { message ->
                    message?.let { value = it }
                }
            }
        }
    }

    fun firstError(): String? {
        for (validator in validators) {
            if (!validator.isValid()) {
                return validator.error.value ?: ""
            }
        }

        return null
    }

    fun validateForm() {
        isValidMediator.value = validate()
    }

    private fun validate(): Boolean {
        for (validator in validators) {
            if (!validator.isValid())
                return false
        }

        return true
    }
}