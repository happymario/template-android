package com.victoria.bleled.app.user

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.victoria.bleled.R
import com.victoria.bleled.app.main.MainActivity
import com.victoria.bleled.common.Constants
import com.victoria.bleled.common.MediaManager
import com.victoria.bleled.data.model.ModelUpload
import com.victoria.bleled.data.remote.NetworkObserver
import com.victoria.bleled.databinding.ActivitySignupBinding
import com.victoria.bleled.util.CommonUtil
import com.victoria.bleled.util.arch.base.BaseBindingActivity
import com.victoria.bleled.util.arch.network.NetworkResult
import com.victoria.bleled.util.feature.PermissionUtil
import com.victoria.bleled.util.kotlin_ext.getViewModelFactory
import com.victoria.bleled.util.thirdparty.glide.ImageLoader
import java.io.File

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
     *  Event Handler
     ************************************************************/
    fun onBack(view: View) {
        finish()
    }

    fun onProfile(view: View) {
        checkPermissionsForMediaManager()
    }

    fun onSignup(view: View) {
        var email = binding.etId.text.toString()
        var pwd = binding.etPwd.text.toString()
        var pwdConfirm = binding.etPwdConfrim.text.toString()
        var motto = binding.etMotto.text.toString()

        if (!CommonUtil.isValidEmail(email)) {
            CommonUtil.showToast(this, R.string.input_valid_email)
            return
        }

        if (pwd.length < 6) {
            CommonUtil.showToast(this, R.string.input_valid_pwd)
            return
        }

        if (pwd != pwdConfirm) {
            CommonUtil.showToast(this, R.string.hint_pwd_confirm)
            return
        }

        reqSignup(email, pwd, motto)
    }


    /************************************************************
     *  Helpers
     ************************************************************/
    override fun initView() {
        super.initView()

        binding.llContent.setOnClickListener {
            hideKeyboard()
        }
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

                uploadFile(file)
            }

            override fun onVideo(video: Uri?, thumb: Uri?, thumbBitmap: Bitmap?) {
                val imageView = binding.ivPhoto
                imageView.setImageBitmap(thumbBitmap)

                val file = MediaManager.externalUriToFile(this@SignupActivity, video)
                CommonUtil.showToast(this@SignupActivity, video.toString())
                CommonUtil.showToast(this@SignupActivity, "FileExist => ${file.exists()}")

                uploadFile(file)
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

    /************************************************************
     *  Networking
     ************************************************************/
    private fun reqSignup(email: String, pwd: String, motto: String) {
        // TODO Login
        goMain()
    }


    private fun uploadFile(file: File) {
        viewModel.uploadFile(file).observe(this, object : NetworkObserver<ModelUpload>(this@SignupActivity, true) {
            override fun onChanged(result: NetworkResult<ModelUpload>) {
                super.onChanged(result)

                if (result.status.value == NetworkResult.Status.loading) {
                    showProgress()
                } else {
                    hideProgress()

                    if (result.status.value == NetworkResult.Status.success) {
                        ImageLoader.loadImage(
                            this@SignupActivity,
                            binding.ivPhoto,
                            R.drawable.xml_bg_default_img,
                            result.data.file_url
                        )
                    }
                }
            }
        })
    }

    /************************************************************
     *  SubClasses
     ************************************************************/

}
