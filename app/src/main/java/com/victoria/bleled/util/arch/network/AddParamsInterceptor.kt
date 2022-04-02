package com.victoria.bleled.util.arch.network

import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.Response
import okio.BufferedSink
import java.io.IOException
import java.nio.charset.Charset

public class AddParamsInterceptor(
    getMap: HashMap<String, String>,
    postMap: HashMap<String, String>,
) :
    Interceptor {
    private var mGETMap = getMap
    private var mPOSTMap = postMap

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val originalHttpUrl = original.url
        val urlBuilder = originalHttpUrl.newBuilder()
        val requestBuilder = original.newBuilder()

        for ((key, value) in mGETMap.entries) {
            urlBuilder.addQueryParameter(key, value)
        }

        val httpUrl = urlBuilder.build()

        if (original.body != null) {
            val requestBody = original.body
            val paramList = mPOSTMap.entries.map { "&" + it.key + "=" + it.value }
            val paramLength = paramList.map { it.length }.sum()

            val newRequestBody = object : RequestBody() {
                override fun contentLength(): Long =
                    (requestBody?.contentLength() ?: 0) + paramLength

                override fun contentType(): MediaType? = requestBody?.contentType()

                @Throws(IOException::class)
                override fun writeTo(sink: BufferedSink) {
                    requestBody?.writeTo(sink)
                    for (param in paramList) {
                        sink.writeString(param, Charset.forName("UTF-8"))
                    }
                }
            }

            requestBuilder.post(newRequestBody)
        }

        requestBuilder.url(httpUrl)

        val request = requestBuilder.build()
        return chain.proceed(request)
    }


    class Builder {
        private val mGETMap = hashMapOf<String, String>()
        private val mPOSTMap = hashMapOf<String, String>()

        /**
         * add (key, value) into additional common parameter with @Query annotation
         */
        fun addQueryParameter(key: String, value: String) = this.apply { mGETMap[key] = value }

        /**
         * add (key, value) into additional common parameter with @Field annotation
         */
        fun addFieldParameter(key: String, value: String) = this.apply { mPOSTMap[key] = value }

        /**
         * add (key, value) into additional common parameter with @Query, @Field annotation
         */
        fun addParameter(key: String, value: String) =
            this.apply { addQueryParameter(key, value).addFieldParameter(key, value) }

        /**
         * Building AddParamsInterceptor
         */
        fun build() = AddParamsInterceptor(mGETMap, mPOSTMap)
    }
}