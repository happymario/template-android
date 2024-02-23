package com.mario.template.ui.auth

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.mario.lib.base.architecture.Resource
import com.mario.template.Constants
import com.mario.template.R
import com.mario.template.base.BaseLayoutBindingActivity
import com.mario.template.common.manager.MediaManager
import com.mario.template.data.model.User
import com.mario.template.databinding.ActivitySignupBinding
import com.mario.template.helper.CommonHelper
import com.mario.template.helper.PermissionHelper
import com.mario.template.ui.layout.LayoutMainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignupActivity : BaseLayoutBindingActivity<ActivitySignupBinding>() {
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
    private val viewModel by viewModels<SignupViewModel>()
    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            if (PermissionHelper.isPermisionsRevoked(this, CAMERA_GALLERY_PERMISSIONS)) {
                PermissionHelper.showPermissionGuide(this, 0)
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

//        viewModel.toastMessage.observe(this, { msg ->
//            CommonUtil.showToast(this, msg)
//        })
//
//        viewModel.dataLoading.observe(this, { loading ->
//            if (loading) {
//                showProgress()
//            } else {
//                hideProgress()
//            }
//        })
//
//        viewModel.networkErrorLiveData.observe(this, { error ->
//            val exception = error.error
//            if (exception is ApiException && exception.code == ApiException.ERR_NO_USER) {
//                CommonUtil.showToast(this, R.string.msg_no_login_user)
//            } else {
//                val msg = NetworkObserver.getErrorMsg(this, error)
//                CommonUtil.showToast(
//                    this,
//                    if (msg == null || msg.isEmpty()) getString(R.string.network_connect_error) else msg
//                )
//            }
//        })

        viewModel.loginCompleteEvent.observe(this) {
            goMain()
        }

        viewModel.signupUser.observe(this) {
            if (it != Resource.Loading<User>()) {
                showProgress()
            } else if (it != Resource.Error<User>()) {
                CommonHelper.showToast(this, it.message)
            } else if (it != Resource.None<User>()) {
                hideProgress()
            } else {
                CommonHelper.showToast(this, "회원가입이 완료되었습니다.")
            }
        }
    }


    private fun initMediaManager() {
        mediaManager =
            MediaManager(this, false)
        mediaManager.setCropEnable(true)
        mediaManager.setMediaCallback(object :
            MediaManager.MediaCallback {
            override fun onDelete() {
                val imageView = binding.ivPhoto
                imageView.setImageResource(R.drawable.bg_profile)
            }

            override fun onFailed(code: Int, err: String?) {
                CommonHelper.showToast(this@SignupActivity, "Error => code($code), err($err)")
            }

            override fun onImage(uri: Uri?, bitmap: Bitmap?) {
                //val imageView = binding.ivPhoto
                //imageView.setImageBitmap(bitmap)

                val file = mediaManager.getFileFromUri(uri)
                CommonHelper.showToast(this@SignupActivity, uri.toString())
                CommonHelper.showToast(this@SignupActivity, "FileExist => ${file?.exists()}")

                viewModel.uploadFile(file)
            }

            override fun onVideo(video: Uri?, thumb: Uri?, thumbBitmap: Bitmap?) {
                val imageView = binding.ivPhoto
                if (thumbBitmap != null) {
                    imageView.setImageBitmap(thumbBitmap)
                }

                val file = MediaManager.externalUriToFile(this@SignupActivity, video)
                CommonHelper.showToast(this@SignupActivity, video.toString())
                CommonHelper.showToast(this@SignupActivity, "FileExist => ${file.exists()}")

                viewModel.uploadFile(file)
            }
        })
    }


    private fun hideKeyboard() {
        CommonHelper.hideKeyboard(binding.etId)
        CommonHelper.hideKeyboard(binding.etPwd)
        CommonHelper.hideKeyboard(binding.etPwdConfrim)
        CommonHelper.hideKeyboard(binding.etMotto)
    }

    private fun goMain() {
        val intent = Intent(this, LayoutMainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            putExtra(Constants.ARG_TYPE, true)
        }
        startActivity(intent)
    }

    private fun checkPermissionsForMediaManager() {
        if (PermissionHelper.hasPermission(this, CAMERA_GALLERY_PERMISSIONS)) {
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
