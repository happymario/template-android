package com.victoria.bleled.util.feature.form

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData

abstract class FormValidator {
    abstract val validators: List<FormDataValidator>

    var mediator = MediatorLiveData<Boolean>()

    val errors by lazy {
        MediatorLiveData<String?>().apply {
            for (validator in validators) {
                addSource(validator.error) { message ->
                    message?.let { value = it }
                }
            }
        }
    }

    // 하나만 변경되도 전체의 상태를 다시 점검
    fun bind(fields: List<MutableLiveData<*>>) {
        fields.forEach { field ->
            mediator.addSource(field) { validate() }
        }
    }

    fun finds(liveData: MutableLiveData<*>): List<FormDataValidator> {
        return validators.filter { it.liveData == liveData }
    }

    fun validateInField(liveData: MutableLiveData<*>): Boolean {
        for (validator in finds(liveData)) {
            if (!validator.isValid())
                return false
        }

        return true
    }

    fun error(): String? {
        for (validator in validators) {
            if (!validator.isValid()) {
                return validator.error.value ?: ""
            }
        }
        return null
    }

    fun errorInField(liveData: MutableLiveData<*>): String? {
        for (validator in finds(liveData)) {
            if (!validator.isValid()) {
                return validator.error.value ?: ""
            }
        }
        return null
    }


    fun validate() {
        mediator.value = validateLoop()
    }

    private fun validateLoop(): Boolean {
        for (validator in validators) {
            if (!validator.isValid())
                return false
        }

        return true
    }
}