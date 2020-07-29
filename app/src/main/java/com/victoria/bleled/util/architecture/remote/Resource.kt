/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.victoria.bleled.util.architecture.remote

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData


/**
 * A generic class that holds a value with its loading status.
 * @param <T>
</T> */
open class Resource<T> {
    var status: LiveData<Status> = MediatorLiveData()
    var data: T? = null
    var message: String? = null

    constructor(status: Status, data: T?, message: String?) {
        this.status = MutableLiveData(status)
        this.data = data
        this.message = message
    }

    constructor(status: LiveData<Status>, data: T?, message: String?) {
        this.status = status
        this.data = data
        this.message = message
    }

    companion object {
        fun <T> success(data: T?): Resource<T> {
            return Resource(
                Status.SUCCESS,
                data,
                null
            )
        }

        fun <T> error(msg: String, data: T?): Resource<T> {
            return Resource(
                Status.ERROR,
                data,
                msg
            )
        }

        fun <T> loading(data: T?): Resource<T> {
            return Resource(
                Status.LOADING,
                data,
                null
            )
        }
    }
}
