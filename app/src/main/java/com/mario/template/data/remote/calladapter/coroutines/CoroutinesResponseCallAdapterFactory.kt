package com.mario.template.data.remote.calladapter.coroutines

import com.mario.template.data.remote.response.ApiResponse
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class CoroutinesResponseCallAdapterFactory : CallAdapter.Factory() {

    override fun get(
        returnType: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit,
    ) = when (getRawType(returnType)) {
        Call::class.java -> {
            val callType = getParameterUpperBound(0, returnType as ParameterizedType)
            when (getRawType(callType)) {
                ApiResponse::class.java -> {
                    val resultType = getParameterUpperBound(0, callType as ParameterizedType)
                    CoroutinesResponseCallAdapter(resultType)
                }

                else -> null
            }
        }

        else -> null
    }
}