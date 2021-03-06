package com.victoria.bleled.app.test

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import com.victoria.bleled.R
import com.victoria.bleled.common.ExternalFolderManager
import com.victoria.bleled.common.MediaManager
import com.victoria.bleled.util.CommonUtil
import com.victoria.bleled.util.feature.PermissionUtil

class CameraTestActivity : AppCompatActivity() {

    private val cameraPermissions = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    private val gallaryPermissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

    private val RC_CAMERA_PERMS = 2001
    private val RC_GALLARY_PERMS = 2002
    private var mMediaManager: MediaManager? = null
    private var mStorageManager: ExternalFolderManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_camera)

        initMediaManager()
        mStorageManager = ExternalFolderManager(this, "template")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == MediaManager.REQ_CROP_IMAGE
            || requestCode == MediaManager.REQ_SET_CAMERA
            || requestCode == MediaManager.REQ_SET_GALLERY
            || requestCode == MediaManager.REQ_SET_CAMERA_VIDEO
        ) {
            mMediaManager?.onActivityResult(requestCode, resultCode, data)
        } else if (requestCode == RC_CAMERA_PERMS) {
            if (PermissionUtil.isPermisionsRevoked(
                    this,
                    cameraPermissions
                )
            ) {
                PermissionUtil.showPermissionGuide(
                    this,
                    requestCode
                )
            } else {
                checkCameraPermissions()
            }
        } else if (requestCode == RC_GALLARY_PERMS) {
            if (PermissionUtil.isPermisionsRevoked(
                    this,
                    gallaryPermissions
                )
            ) {
                PermissionUtil.showPermissionGuide(
                    this,
                    requestCode
                )
            } else {
                checkGallaryPermissions()
            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        @NonNull permissions: Array<String>,
        @NonNull grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == RC_CAMERA_PERMS) {
            if (PermissionUtil.isPermisionsRevoked(
                    this,
                    cameraPermissions
                )
            ) {
                PermissionUtil.showPermissionGuide(
                    this,
                    requestCode
                )
            } else {
                checkCameraPermissions()
            }
        } else if (requestCode == RC_GALLARY_PERMS) {
            if (PermissionUtil.isPermisionsRevoked(
                    this,
                    gallaryPermissions
                )
            ) {
                PermissionUtil.showPermissionGuide(
                    this,
                    requestCode
                )
            } else {
                checkGallaryPermissions()
            }
        }
    }


    private fun initMediaManager() {
        mMediaManager = MediaManager(this, false)
        mMediaManager?.setCropEnable(true)
        mMediaManager?.setMediaCallback(object :
            MediaManager.MediaCallback {
            override fun onDelete() {
                val imageView = findViewById<ImageView>(R.id.imageView)
                imageView.setImageResource(R.drawable.ic_launcher_background)
            }

            override fun onFailed(code: Int, err: String?) {
                CommonUtil.showToast(this@CameraTestActivity, "Error => code($code), err($err)")
            }

            override fun onImage(uri: Uri?, bitmap: Bitmap?) {
                val imageView = findViewById<ImageView>(R.id.imageView)
                imageView.setImageBitmap(bitmap)

                val file = mMediaManager?.getFileFromUri(uri)
                CommonUtil.showToast(this@CameraTestActivity, uri.toString())
                CommonUtil.showToast(this@CameraTestActivity, "FileExist => ${file?.exists()}")

                val output = mStorageManager?.saveFileToExternal(file)
                if (output != null) {
                    CommonUtil.showToast(
                        this@CameraTestActivity,
                        "External => ${output?.toString()}"
                    )
                }

                val otherOutput = mStorageManager?.createBitmapUri(bitmap)
                if (otherOutput != null) {
                    CommonUtil.showToast(
                        this@CameraTestActivity,
                        "External2 => ${otherOutput.toString()}"
                    )
                }
            }

            override fun onVideo(video: Uri?, thumb: Uri?, thumbBitmap: Bitmap?) {
                val imageView = findViewById<ImageView>(R.id.imageView)
                imageView.setImageBitmap(thumbBitmap)

                val file = mMediaManager?.getFileFromUri(video)
                CommonUtil.showToast(this@CameraTestActivity, video.toString())
                CommonUtil.showToast(this@CameraTestActivity, "FileExist => ${file?.exists()}")
            }
        })
    }


    private fun checkCameraPermissions() {
        if (PermissionUtil.hasPermission(
                this,
                cameraPermissions
            )
        ) {
            mMediaManager?.openCamera()
        } else {
            PermissionUtil.requestPermission(
                this,
                cameraPermissions,
                RC_CAMERA_PERMS
            )
        }
    }

    private fun checkGallaryPermissions() {
        if (PermissionUtil.hasPermission(
                this,
                gallaryPermissions
            )
        ) {
            mMediaManager?.openGallery()
        } else {
            PermissionUtil.requestPermission(
                this,
                gallaryPermissions,
                RC_GALLARY_PERMS
            )
        }
    }

    fun onCamera(view: View) {
        checkCameraPermissions()
    }

    fun onGallary(view: View) {
        checkGallaryPermissions()
    }

    fun onVideo(view: View) {
        mMediaManager?.openCamera(true)
    }

    fun onFinish(view: View) {
        finish()
    }

}
