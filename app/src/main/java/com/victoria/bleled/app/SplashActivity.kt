package com.victoria.bleled.app

import android.Manifest
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.AnticipateInterpolator
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.animation.doOnEnd
import androidx.lifecycle.Observer
import com.victoria.bleled.R
import com.victoria.bleled.app.main.MainActivity
import com.victoria.bleled.app.user.LoginActivity
import com.victoria.bleled.common.Constants
import com.victoria.bleled.data.remote.NetworkObserver
import com.victoria.bleled.util.CommonUtil
import com.victoria.bleled.util.arch.EventObserver
import com.victoria.bleled.util.arch.base.BaseActivity
import com.victoria.bleled.util.feature.PermissionUtil
import com.victoria.bleled.util.kotlin_ext.getViewModelFactory
import java.time.Duration
import java.time.Instant


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

        initView()
        initViewModel()
        checkPermissions()
    }


    /************************************************************
     *  Private
     ************************************************************/
    private fun checkPermissions() {
        if (PermissionUtil.hasPermission(this, requiredPermissions)) {
            viewModel.start()
        } else {
            val arrPermission = requiredPermissions + optionalPermissions
            permissionLauncher.launch(arrPermission)
        }
    }

    private fun initView() {
        // Set up an OnPreDrawListener to the root view.
        val content: View = findViewById(android.R.id.content)
        content.viewTreeObserver.addOnPreDrawListener(
            object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    // Check if the initial data is ready.
                    return if (viewModel.isReady.value == true) {
                        // The content is ready; start drawing.
                        content.viewTreeObserver.removeOnPreDrawListener(this)
                        true
                    } else {
                        // The content is not ready; suspend.
                        false
                    }
                }
            }
        )

        // Add a callback that's called when the splash screen is animating to
        // the app content.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            splashScreen.setOnExitAnimationListener { splashScreenView ->

                // Get the duration of the animated vector drawable.
                val animationDuration = splashScreenView.iconAnimationDuration
                // Get the start time of the animation.
                val animationStart = splashScreenView.iconAnimationStart
                // Calculate the remaining duration of the animation.
                val remainingDuration = if (animationDuration != null && animationStart != null) {
                    (animationDuration - Duration.between(animationStart, Instant.now()))
                        .toMillis()
                        .coerceAtLeast(0L)
                } else {
                    0L
                }
                Log.e("test", "animationDuration - $remainingDuration")
                Log.e("test", "animationStart - $remainingDuration")
                Log.e("test", "remainingDuration - $remainingDuration")

                // Create your custom animation.
                val slideUp = ObjectAnimator.ofFloat(
                    splashScreenView,
                    View.TRANSLATION_Y,
                    0f,
                    -splashScreenView.height.toFloat()
                )
                slideUp.interpolator = AnticipateInterpolator()
                slideUp.duration = 200L

                // Call SplashScreenView.remove at the end of your custom animation.
                slideUp.doOnEnd { splashScreenView.remove() }

                // Run your animation.
                slideUp.start()
            }
        }
    }

    private fun initViewModel() {
        viewModel.dataLoading.observe(this, Observer {
            if (it == true) {
                showProgress()
            } else {
                hideProgress()
            }
        })

        viewModel.networkErrorLiveData.observe(this, Observer { error ->
            val msg = NetworkObserver.getErrorMsg(this, error)
            CommonUtil.showToast(
                this,
                if (msg == null || msg.isEmpty()) getString(R.string.network_connect_error) else msg
            )
        })

        viewModel.openEvent.observe(this, EventObserver {
            if (it == 1) {
                goMain()
            } else {
                goLogin()
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