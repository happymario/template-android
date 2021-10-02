package com.victoria.bleled.app

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.victoria.bleled.R
import com.victoria.bleled.app.main.MainActivity
import com.victoria.bleled.app.user.LoginActivity
import com.victoria.bleled.common.Constants
import com.victoria.bleled.data.DataRepository
import com.victoria.bleled.data.remote.NetworkObserver
import com.victoria.bleled.util.CommonUtil
import com.victoria.bleled.util.arch.base.BaseActivity
import com.victoria.bleled.util.arch.network.NetworkResult
import com.victoria.bleled.util.feature.PermissionUtil
import com.victoria.bleled.util.kotlin_ext.getViewModelFactory


class SplashActivity : BaseActivity() {
    /************************************************************
     *  Static & Global Members
     ************************************************************/
    companion object {
        private val requiredPermissions = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        private val optionalPermissions = arrayOf(
            Manifest.permission.CAMERA
        )
    }


    /************************************************************
     *  UI controls & Data members
     ************************************************************/
    private val viewModel by viewModels<SplashViewModel> { getViewModelFactory() }
    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            if (PermissionUtil.isPermisionsRevoked(this, requiredPermissions)) {
                PermissionUtil.showPermissionGuide(this, 0)
            } else {
                checkPermissions()
            }
        }

    /************************************************************
     *  Overrides
     ************************************************************/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        checkPermissions()
    }


    /************************************************************
     *  Private
     ************************************************************/
    private fun checkPermissions() {
        if (PermissionUtil.hasPermission(this, requiredPermissions)) {
            startLogic()
        } else {
            val arrPermission = requiredPermissions + optionalPermissions
            permissionLauncher.launch(arrPermission)
        }
    }

    private fun startLogic() {
        viewModel.reqGetAppInfo().observe(this, Observer {
            if (it == null) {
                return@Observer
            }

            if (it.status.value == NetworkResult.Status.loading) {
                showProgress()
            } else {
                hideProgress()

                if (it.status.value == NetworkResult.Status.success) {
                    val prefDataSource =
                        DataRepository.provideDataRepository(this).prefDataSource
                    prefDataSource.appInfo = it.data

                    Handler(Looper.getMainLooper()).postDelayed({
                        if (prefDataSource.user != null) {
                            goMain()
                        } else {
                            goLogin()
                        }
                    }, 2000)
                } else {
                    val msg = NetworkObserver.getErrorMsg(this, it)
                    CommonUtil.showToast(
                        this,
                        if (msg == null || msg.isEmpty()) getString(R.string.network_connect_error) else msg
                    )

                    if(Constants.IS_TEST) {
                        goLogin()
                    }
                }
            }
        })
    }

    private fun goLogin() {
        val intent = Intent(this, LoginActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        startActivity(intent)
    }

    private fun goMain() {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            putExtra(Constants.ARG_TYPE, true)
        }
        startActivity(intent)
    }
}