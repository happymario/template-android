/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ex)
 * private fun checkBioPrompt() {
        val canAuthenticate = BiometricManager.from(applicationContext).canAuthenticate()
        if (canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS) {
            val biometricPrompt =
                BiometricPromptUtils.createBiometricPrompt(this) { result ->
                    setResult(RESULT_OK)
                    finish()
                }
            val promptInfo = BiometricPromptUtils.createPromptInfo(this)
            biometricPrompt.authenticate(promptInfo)
        }
    }
 */

package com.mario.template.helper

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.mario.template.R

// Since we are using the same methods in more than one Activity, better give them their own file.
object BiometricPromptHelper {
    private const val TAG = "BiometricPromptHelper"
    fun createBiometricPrompt(
        activity: AppCompatActivity,
        processSuccess: (BiometricPrompt.AuthenticationResult) -> Unit,
    ): BiometricPrompt {
        val executor = ContextCompat.getMainExecutor(activity)

        val callback = object : BiometricPrompt.AuthenticationCallback() {

            override fun onAuthenticationError(errCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errCode, errString)
                Log.d(TAG, "errCode is $errCode and errString is: $errString")
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Log.d(TAG, "User biometric rejected.")
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                Log.d(TAG, "Authentication was successful")
                processSuccess(result)
            }
        }
        return BiometricPrompt(activity, executor, callback)
    }

    fun createPromptInfo(activity: AppCompatActivity): BiometricPrompt.PromptInfo =
        BiometricPrompt.PromptInfo.Builder().apply {
            setTitle(activity.getString(R.string.app_name))
            setConfirmationRequired(false)
            setNegativeButtonText(activity.getString(R.string.cancel))
        }.build()
}