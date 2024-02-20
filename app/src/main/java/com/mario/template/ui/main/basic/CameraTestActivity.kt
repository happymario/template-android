package com.mario.template.ui.main.basic

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.RectF
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.annotation.NonNull
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.mario.lib.base.view.imageview.TouchImageView
import com.mario.template.Constants
import com.mario.template.R
import com.mario.template.common.AppExecutors
import com.mario.template.common.manager.ExternalFolderManager
import com.mario.template.common.manager.MediaManager
import com.mario.template.helper.CommonHelper
import com.mario.template.helper.PermissionHelper
import com.mario.template.helper.gallary.Gallary
import java.io.File

class CameraTestActivity : AppCompatActivity() {

    private val cameraPermissions = arrayOf(
        Manifest.permission.CAMERA,
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) Manifest.permission.READ_MEDIA_IMAGES else Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    private val gallaryPermissions =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) arrayOf(
            Manifest.permission.READ_MEDIA_IMAGES
        ) else arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

    private val RC_CAMERA_PERMS = 2001
    private val RC_GALLARY_PERMS = 2002
    private var mMediaManager: MediaManager? = null
    private var mStorageManager: ExternalFolderManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_camera)

        initMediaManager()
        mStorageManager =
            ExternalFolderManager(
                this,
                "template"
            )
        setupToolbar()
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId === android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
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
            if (PermissionHelper.isPermisionsRevoked(
                    this,
                    cameraPermissions
                )
            ) {
                PermissionHelper.showPermissionGuide(
                    this,
                    requestCode
                )
            } else {
                checkCameraPermissions()
            }
        } else if (requestCode == RC_GALLARY_PERMS) {
            if (PermissionHelper.isPermisionsRevoked(
                    this,
                    gallaryPermissions
                )
            ) {
                PermissionHelper.showPermissionGuide(
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
        @NonNull grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == RC_CAMERA_PERMS) {
            if (PermissionHelper.isPermisionsRevoked(
                    this,
                    cameraPermissions
                )
            ) {
                PermissionHelper.showPermissionGuide(
                    this,
                    requestCode
                )
            } else {
                checkCameraPermissions()
            }
        } else if (requestCode == RC_GALLARY_PERMS) {
            if (PermissionHelper.isPermisionsRevoked(
                    this,
                    gallaryPermissions
                )
            ) {
                PermissionHelper.showPermissionGuide(
                    this,
                    requestCode
                )
            } else {
                checkGallaryPermissions()
            }
        }
    }


    private fun initMediaManager() {
        mMediaManager =
            MediaManager(this, true)
        mMediaManager?.setCropEnable(true)
        mMediaManager?.setMediaCallback(object :
            MediaManager.MediaCallback {
            override fun onDelete() {
                val imageView = findViewById<ImageView>(R.id.imageView)
                imageView.setImageResource(R.drawable.bg_profile)
            }

            override fun onFailed(code: Int, err: String?) {
                CommonHelper.showToast(this@CameraTestActivity, "Error => code($code), err($err)")
            }

            override fun onImage(uri: Uri?, bitmap: Bitmap?) {
                val imageView = findViewById<ImageView>(R.id.imageView)
                imageView.setImageBitmap(bitmap)

                val file = mMediaManager?.getFileFromUri(uri)
                CommonHelper.showToast(this@CameraTestActivity, uri.toString())
                CommonHelper.showToast(this@CameraTestActivity, "FileExist => ${file?.exists()}")

                val output = file?.let { mStorageManager?.saveFileToExternal(it) }
                if (output != null) {
                    CommonHelper.showToast(
                        this@CameraTestActivity,
                        "External => $output"
                    )
                }

                val otherOutput = bitmap?.let { mStorageManager?.createBitmapUri(it) }
                if (otherOutput != null) {
                    CommonHelper.showToast(
                        this@CameraTestActivity,
                        "External2 => ${otherOutput.toString()}"
                    )
                }
            }

            override fun onVideo(video: Uri?, thumb: Uri?, thumbBitmap: Bitmap?) {
                val imageView = findViewById<ImageView>(R.id.imageView)
                imageView.setImageBitmap(thumbBitmap)

                val file = mMediaManager?.getFileFromUri(video)
                CommonHelper.showToast(this@CameraTestActivity, video.toString())
                CommonHelper.showToast(this@CameraTestActivity, "FileExist => ${file?.exists()}")
            }
        })
    }


    private fun checkCameraPermissions() {
        if (PermissionHelper.hasPermission(
                this,
                cameraPermissions
            )
        ) {
            mMediaManager?.openCamera()
        } else {
            PermissionHelper.requestPermission(
                this,
                cameraPermissions,
                RC_CAMERA_PERMS
            )
        }
    }

    private fun checkGallaryPermissions() {
        if (PermissionHelper.hasPermission(
                this,
                gallaryPermissions
            )
        ) {
            mMediaManager?.openGallery()
        } else {
            PermissionHelper.requestPermission(
                this,
                gallaryPermissions,
                RC_GALLARY_PERMS
            )
        }
    }


    private fun setupToolbar() {
        setSupportActionBar(findViewById(R.id.toolbar))

        val ab: ActionBar? = supportActionBar
        ab?.title = resources.getStringArray(R.array.arr_main_tech)[2]
        ab?.setDisplayHomeAsUpEnabled(true)
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

    fun exportSelectedGallery() {
        AppExecutors().diskIO.execute {
            var images = arrayListOf<Gallary>()
            var ivSliderImage = findViewById<TouchImageView>(R.id.imageView)
            val rect = ivSliderImage.zoomedRect
            val ratio = ivSliderImage.measuredWidth * 1f / ivSliderImage.measuredHeight

            for (i in 0 until images.size) {
                val image = images[i]
                val file = MediaManager.externalUriToFile(
                    this@CameraTestActivity,
                    image.imageUri
                )
                val options = BitmapFactory.Options()
                options.inPreferredConfig = Bitmap.Config.ARGB_8888
                var srcBitmap = BitmapFactory.decodeFile(file.absolutePath, options)

                // 90도이미지이면 회전시키기
                srcBitmap =
                    mMediaManager?.rotateBitmapUri(Uri.fromFile(File(file.absolutePath)), srcBitmap)

                // zoom하기
                var centerBitmap = srcBitmap
                val zoom = ivSliderImage.currentZoom
                val zoomWidth = (centerBitmap.width * zoom).toInt()
                val zoomHeight = (centerBitmap.height * zoom).toInt()
                var zoomBitmap = centerBitmap
                if (zoom != 1.0f) {
                    zoomBitmap =
                        Bitmap.createScaledBitmap(centerBitmap, zoomWidth, zoomHeight, false)
                }

                // zoom영역에서의 crop
                val cropRect = RectF(
                    rect.left * zoomWidth,
                    rect.top * zoomHeight,
                    rect.right * zoomWidth,
                    rect.bottom * zoomHeight
                )
                var lastBitmap = Bitmap.createBitmap(
                    zoomBitmap,
                    cropRect.left.toInt(),
                    cropRect.top.toInt(),
                    cropRect.width().toInt(),
                    cropRect.height().toInt()
                )


                // center crop bitmap
                var newWidth = lastBitmap.width
                var newHeight = (lastBitmap.width / ratio).toInt()
                if (newHeight <= lastBitmap.height) {
                    lastBitmap = Bitmap.createBitmap(
                        lastBitmap,
                        0,
                        (lastBitmap.height - newHeight) / 2,
                        newWidth,
                        newHeight
                    )
                } else {
                    newWidth = (lastBitmap.height * ratio).toInt()
                    newHeight = lastBitmap.height

                    lastBitmap = Bitmap.createBitmap(
                        lastBitmap,
                        ((lastBitmap.width - newWidth) / 2),
                        0,
                        newWidth,
                        newHeight
                    )
                }

                val fileUri = mMediaManager?.createFileUri(lastBitmap)

                val backup = image.path
                images[i].id = 0
                images[i].path = fileUri.toString()
                images[i].content = backup
            }

            val intent = Intent()
            intent.putExtra(Constants.ARG_DATA, images)
            setResult(RESULT_OK, intent)
            finish()
        }
    }

}
