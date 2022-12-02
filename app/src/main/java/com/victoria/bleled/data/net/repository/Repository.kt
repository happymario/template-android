package com.victoria.bleled.data.net.repository

import com.victoria.bleled.base.internal.AppException
import com.victoria.bleled.data.net.adapter.ApiResponse
import com.victoria.bleled.data.net.adapter.onError
import com.victoria.bleled.data.net.adapter.onException


abstract class Repository {

    companion object {

    }

    fun <T> ApiResponse<T>.nextThrow() {
        this.onError {
            throw AppException.ServerHttp(statusCode.code, this.response.message())
        }.onException {
            throw AppException.Network(this.exception)
        }
    }
}