package com.mario.template.data.remote.response

import com.mario.template.data.exception.AppException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

object ResponseTransformer {
    suspend fun <T> transformBaseResponse(call: Flow<BaseResponse<T>>) = flow {
        try {
            call.collect {
                if (it.code == 0 && it.data != null) {
                    emit(it.data)
                } else {
                    throw AppException.ServerHttp(it.code ?: 0, it.message ?: "")
                }
            }
        } catch (e: Throwable) {
            throw AppException.Network(e)
        }
    }

    suspend fun <T> transformBaseResponseNullable(call: Flow<BaseResponse<T?>>) = flow {
        try {
            call.collect {
                if (it.code == 0) {
                    emit(it.data)
                } else {
                    throw AppException.ServerHttp(it.code ?: 0, it.message ?: "")
                }
            }
        } catch (e: Throwable) {
            throw AppException.Network(e)
        }
    }
}