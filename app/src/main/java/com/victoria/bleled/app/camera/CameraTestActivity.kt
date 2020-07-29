package com.victoria.bleled.app.camera

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
import com.victoria.bleled.common.MediaManager
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_test)

        initMediaManager()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == MediaManager.CROP_IMAGE
            || requestCode == MediaManager.SET_CAMERA
            || requestCode == MediaManager.SET_GALLERY
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
        mMediaManager = MediaManager(this, true)
        mMediaManager?.setMediaCallback(object :
            MediaManager.MediaCallback {
            override fun onDelete() {
                val imageView = findViewById<ImageView>(R.id.imageView)
                imageView.setImageResource(R.drawable.ic_launcher_background)
            }

            override fun onFailed(code: Int, err: String?) {

            }

            override fun onSelected(
                isVideo: Boolean?,
                uri: Uri?,
                bitmap: Bitmap?,
                videoPath: String?,
                thumbPath: String?
            ) {
                val imageView = findViewById<ImageView>(R.id.imageView)
                imageView.setImageBitmap(bitmap)
            }
        })
    }


    private fun checkCameraPermissions() {
        if (PermissionUtil.hasPermission(
                this,
                cameraPermissions
            )
        ) {
            mMediaManager?.getImageFromCamera()
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
            mMediaManager?.getMediaFromGallery()
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

    fun onFinish(view: View) {
        finish()
    }

}
