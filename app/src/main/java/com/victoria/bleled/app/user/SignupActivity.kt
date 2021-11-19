package com.victoria.bleled.app.user

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.victoria.bleled.R
import com.victoria.bleled.app.main.MainActivity
import com.victoria.bleled.common.Constants
import com.victoria.bleled.common.MediaManager
import com.victoria.bleled.data.remote.ApiException
import com.victoria.bleled.data.remote.NetworkObserver
import com.victoria.bleled.databinding.ActivitySignupBinding
import com.victoria.bleled.util.CommonUtil
import com.victoria.bleled.util.arch.base.BaseBindingActivity
import com.victoria.bleled.util.feature.PermissionUtil
import com.victoria.bleled.util.kotlin_ext.getViewModelFactory

class SignupActivity : BaseBindingActivity<ActivitySignupBinding>() {
    /************************************************************
     *  Static & Global Members
     ************************************************************/
    companion object {
        private val CAMERA_GALLERY_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }

    /************************************************************
     *  UI controls & Data members
     ************************************************************/
    private val viewModel by viewModels<UserViewModel> { getViewModelFactory() }
    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            if (PermissionUtil.isPermisionsRevoked(this, CAMERA_GALLERY_PERMISSIONS)) {
                PermissionUtil.showPermissionGuide(this, 0)
            } else {
                checkPermissionsForMediaManager()
            }
        }
    private lateinit var mediaManager: MediaManager

    /************************************************************
     *  Overrides
     ************************************************************/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        initMediaManager()
        initView()
        initViewModel()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == MediaManager.REQ_CROP_IMAGE
            || requestCode == MediaManager.REQ_SET_CAMERA
            || requestCode == MediaManager.REQ_SET_GALLERY
            || requestCode == MediaManager.REQ_SET_CAMERA_VIDEO
        ) {
            mediaManager.onActivityResult(requestCode, resultCode, data)
        }
    }

    /************************************************************
     *  Helpers
     ************************************************************/
    override fun initView() {
        super.initView()

        // views
        hideKeyboard()

        // events
        binding.ibBack.setOnClickListener {
            finish()
        }
        binding.llContent.setOnClickListener {
            hideKeyboard()
        }
        binding.ivPhoto.setOnClickListener {
            checkPermissionsForMediaManager()
        }
    }


    private fun initViewModel() {
        binding.viewmodel = viewModel

        viewModel.toastMessage.observe(this, { msg ->
            CommonUtil.showToast(this, msg)
        })

        viewModel.dataLoading.observe(this, { loading ->
            if (loading) {
                showProgress()
            } else {
                hideProgress()
            }
        })

        viewModel.networkErrorLiveData.observe(this, { error ->
            val exception = error.error
            if (exception is ApiException && exception.code == ApiException.ERR_NO_USER) {
                CommonUtil.showToast(this, R.string.msg_no_login_user)
            } else {
                val msg = NetworkObserver.getErrorMsg(this, error)
                CommonUtil.showToast(
                    this,
                    if (msg == null || msg.isEmpty()) getString(R.string.network_connect_error) else msg
                )
            }
        })

        viewModel.loginCompleteEvent.observe(this, {
            goMain()
        })
    }


    private fun initMediaManager() {
        mediaManager = MediaManager(this, false)
        mediaManager.setCropEnable(true)
        mediaManager.setMediaCallback(object :
            MediaManager.MediaCallback {
            override fun onDelete() {
                val imageView = binding.ivPhoto
                imageView.setImageResource(R.drawable.ic_launcher_background)
            }

            override fun onFailed(code: Int, err: String?) {
                CommonUtil.showToast(this@SignupActivity, "Error => code($code), err($err)")
            }

            override fun onImage(uri: Uri?, bitmap: Bitmap?) {
                //val imageView = binding.ivPhoto
                //imageView.setImageBitmap(bitmap)

                val file = mediaManager.getFileFromUri(uri)
                CommonUtil.showToast(this@SignupActivity, uri.toString())
                CommonUtil.showToast(this@SignupActivity, "FileExist => ${file?.exists()}")

                viewModel.uploadFile(file)
            }

            override fun onVideo(video: Uri?, thumb: Uri?, thumbBitmap: Bitmap?) {
                val imageView = binding.ivPhoto
                imageView.setImageBitmap(thumbBitmap)

                val file = MediaManager.externalUriToFile(this@SignupActivity, video)
                CommonUtil.showToast(this@SignupActivity, video.toString())
                CommonUtil.showToast(this@SignupActivity, "FileExist => ${file.exists()}")

                viewModel.uploadFile(file)
            }
        })
    }


    private fun hideKeyboard() {
        CommonUtil.hideKeyboard(binding.etId)
        CommonUtil.hideKeyboard(binding.etPwd)
        CommonUtil.hideKeyboard(binding.etPwdConfrim)
        CommonUtil.hideKeyboard(binding.etMotto)
    }

    private fun goMain() {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            putExtra(Constants.ARG_TYPE, true)
        }
        startActivity(intent)
    }

    private fun checkPermissionsForMediaManager() {
        if (PermissionUtil.hasPermission(this, CAMERA_GALLERY_PERMISSIONS)) {
            showMediaManager()
        } else {
            permissionLauncher.launch(CAMERA_GALLERY_PERMISSIONS)
        }
    }

    private fun showMediaManager() {
        mediaManager.showSelectPopup(false)
        //mediaManager.openCamera(true)
    }
}
