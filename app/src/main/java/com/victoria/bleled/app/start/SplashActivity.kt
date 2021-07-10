package com.victoria.bleled.app.start

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.Observer
import com.victoria.bleled.R
import com.victoria.bleled.app.login.LoginActivity
import com.victoria.bleled.app.main.MainActivity
import com.victoria.bleled.common.Constants
import com.victoria.bleled.data.DataRepository
import com.victoria.bleled.util.arch.base.BaseActivity
import com.victoria.bleled.util.arch.network.NetworkResult
import com.victoria.bleled.util.feature.PermissionUtil


class SplashActivity : BaseActivity() {
    /************************************************************
     *  Static & Global Members
     ************************************************************/
    private val requiredPermissions = arrayOf(
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    private val optionalPermissions = arrayOf(
        Manifest.permission.BLUETOOTH_ADMIN
    )

    private val requestCodeForPermission = 2001

    /************************************************************
     *  UI controls & Data members
     ************************************************************/
    private var viewModel: SplashViewModel? = null

    /************************************************************
     *  Overrides
     ************************************************************/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        initView()
        initViewModel()

        checkPermissions()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == requestCodeForPermission) {
            if (PermissionUtil.isPermisionsRevoked(this, requiredPermissions)) {
                PermissionUtil.showPermissionGuide(this, requestCode)
            } else {
                checkPermissions()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == requestCodeForPermission) {
            if (PermissionUtil.isPermisionsRevoked(this, requiredPermissions)) {
                PermissionUtil.showPermissionGuide(this, requestCode)
            } else {
                checkPermissions()
            }
        }
    }

    /************************************************************
     *  Event Handler
     ************************************************************/


    /************************************************************
     *  Helpers
     ************************************************************/
    private fun initView() {
        // ui

        // events
    }

    private fun initViewModel() {
        viewModel = SplashViewModel(DataRepository.provideDataRepository())
    }


    private fun checkPermissions() {
        if (PermissionUtil.hasPermission(this, requiredPermissions)) {
            startLogic()
        } else {
            var arrPermision = requiredPermissions + optionalPermissions
            PermissionUtil.requestPermission(this, arrPermision, requestCodeForPermission)
        }
    }

    private fun startLogic() {
        viewModel?.reqGetAppInfo()?.observe(this, Observer {
            it
            if (it == null) {
                return@Observer
            }

            if (it.status.value == NetworkResult.Status.loading) {
                showProgress()
            } else {
                hideProgress()

                if (it.status.value == NetworkResult.Status.success) {
                    val prefDataSource =
                        DataRepository.provideDataRepository().prefDataSource
                    prefDataSource.appInfo = it.data
                }

                Handler(Looper.getMainLooper()).postDelayed({
                    goLogin()
                }, 2000)
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
            putExtra(Constants.INTENT_TYPE, true)
        }
        startActivity(intent)
    }


    /************************************************************
     *  Networking
     ************************************************************/


    /************************************************************
     *  SubClasses
     ************************************************************/
}