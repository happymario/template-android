package com.mario.template.data.remote.calladapter.flow

import kotlinx.coroutines.flow.Flow
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import javax.inject.Inject

class FlowCallAdapterFactory @Inject constructor() : CallAdapter.Factory() {
    override fun get(
        returnType: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit,
    ): CallAdapter<*, *>? {
        check(returnType is ParameterizedType) {
            "Flow return type must be parameterized as Flow<Foo> or Flow<out Foo>"
        }

        if (getRawType(returnType) != Flow::class.java) {
            return null
        }


        val responseType = getParameterUpperBound(0, returnType)
        return FlowCallAdapter<Any>(retrofit, responseType)
    }
}
