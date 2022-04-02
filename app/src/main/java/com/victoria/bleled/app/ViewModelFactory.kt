/*
 * Copyright (C) 2019 The Android Open Source Project
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
package com.victoria.bleled.app

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.victoria.bleled.app.main.MainViewModel
import com.victoria.bleled.app.more.SettingViewModel
import com.victoria.bleled.app.special.bluetooth.BluetoothViewModel
import com.victoria.bleled.app.user.UserViewModel
import com.victoria.bleled.data.DataRepository

/**
 * Factory for all ViewModels.
 */
@Suppress("UNCHECKED_CAST")
class ViewModelFactory constructor(
    private val dataRepository: DataRepository,
    owner: SavedStateRegistryOwner,
    defaultArgs: Bundle? = null,
) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {

    override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle,
    ) = with(modelClass) {
        when {
            isAssignableFrom(SplashViewModel::class.java) ->
                SplashViewModel(dataRepository)
            isAssignableFrom(UserViewModel::class.java) ->
                UserViewModel(dataRepository)
            isAssignableFrom(MainViewModel::class.java) ->
                MainViewModel(dataRepository)
            isAssignableFrom(SettingViewModel::class.java) ->
                SettingViewModel(dataRepository)
            isAssignableFrom(BluetoothViewModel::class.java) ->
                BluetoothViewModel(dataRepository)
            else ->
                throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    } as T
}
