package com.victoria.bleled.app.essential.gallery

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.RectF
import android.net.Uri
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnItemTouchListener
import com.victoria.bleled.R
import com.victoria.bleled.app.BindingAdapters.setImageUrl
import com.victoria.bleled.base.BaseBindingActivity
import com.victoria.bleled.common.AppExecutors
import com.victoria.bleled.common.Constants
import com.victoria.bleled.common.manager.MediaManager
import com.victoria.bleled.databinding.ActivityGallerySelectCropBinding
import com.victoria.bleled.util.CommonUtil
import com.victoria.bleled.util.feature.gallary.Folder
import com.victoria.bleled.util.feature.gallary.Gallary
import com.victoria.bleled.util.feature.gallary.ImageFileLoader
import com.victoria.bleled.util.feature.gallary.ImageLoaderListener
import java.io.File


class GallerySelectCropActivity : BaseBindingActivity<ActivityGallerySelectCropBinding>() {
    /************************************************************
     *  Static & Global Members
     ************************************************************/
    companion object {

    }

    /************************************************************
     *  UI controls & Data members
     ************************************************************/
    private lateinit var imageLoader: ImageFileLoader
    private var folderList = arrayListOf<Folder>()
    private var selectGallaryList = arrayListOf<Gallary>()
    private var mediaManager: MediaManager? = null

    private var folderAdapter: GalleryFolderSpinnerAdapter? = null
    private var gallaryTopAdapter: GalleryTopAdapter? = null
    private var gallaryAdapter: GallerySelectAdapter? = null

    /************************************************************
     *  Overrides
     ************************************************************/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery_select_crop)

        selectGallaryList = intent.getSerializableExtra(Constants.ARG_DATA) as ArrayList<Gallary>
        val imageRatio = intent.getFloatExtra(Constants.ARG_TYPE, 4 / 3f)
        initView()
        setCropRatio(imageRatio)

        mediaManager = MediaManager(this)
        imageLoader = ImageFileLoader(this)

        loadGallery()
    }


    /************************************************************
     *  Events
     ************************************************************/
    fun onBack(view: View) {
        finish()
    }

    fun onCrop(view: View) {
        if (binding.ivPhoto1.visibility == View.VISIBLE) {
            setCropRatio(1f)
        } else {
            setCropRatio(4 / 3f)
        }
    }


    fun onClose(view: View) {
        finish()
    }


    fun onSelect(view: View) {
        val images = selectGallaryList
        if (images.size == 0) {
            val intent = Intent()
            intent.putExtra(Constants.ARG_DATA, images)
            setResult(RESULT_OK, intent)
            finish()
            return
        }

        showProgress()

        AppExecutors().diskIO.execute {
            var ivSliderImage = binding.ivPhoto1
            if (binding.ivPhoto2.visibility == View.VISIBLE) {
                ivSliderImage = binding.ivPhoto2
            }

            val rect = ivSliderImage.zoomedRect
            val ratio = ivSliderImage.measuredWidth * 1f / ivSliderImage.measuredHeight

            for (i in 0 until images.size) {
                val image = images[i]
                val file = MediaManager.externalUriToFile(
                    this@GallerySelectCropActivity,
                    image.imageUri
                )
                val options = BitmapFactory.Options()
                options.inPreferredConfig = Bitmap.Config.ARGB_8888
                var srcBitmap = BitmapFactory.decodeFile(file.absolutePath, options)

                // 90도이미지이면 회전시키기
                srcBitmap =
                    mediaManager?.rotateBitmapUri(Uri.fromFile(File(file.absolutePath)), srcBitmap)

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

                val fileUri = mediaManager?.createFileUri(lastBitmap)

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


    /************************************************************
     *  Privates
     ************************************************************/
    override fun initView() {
        super.initView()
        binding.view = this

        initCropView()
        initFolderList()
        initTopList()
        initGalleryList()

    }

    private fun initCropView() {
        binding.ivPhoto1.setZoom(1.0f)
        binding.ivPhoto1.resetZoom()

        binding.ivPhoto2.setZoom(1.0f)
        binding.ivPhoto2.resetZoom()

        binding.ivPhoto1.visibility = View.VISIBLE
        binding.ivPhoto2.visibility = View.GONE
    }

    private fun initFolderList() {
        folderAdapter = GalleryFolderSpinnerAdapter(this)
        binding.spFolder.adapter = folderAdapter
        binding.spFolder.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long,
            ) {
                // 선택된 folder
                var folder = folderList.get(position)

                gallaryAdapter?.list?.clear()
                gallaryAdapter?.list?.addAll(folder.images)
                gallaryAdapter?.notifyDataSetChanged()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
    }


    private fun initTopList() {
        val recyclerView2 = binding.rvTop
        gallaryTopAdapter = GalleryTopAdapter()
        recyclerView2.adapter = gallaryTopAdapter
        gallaryTopAdapter?.listener = Unit@{ type, idx ->
            val gallery = gallaryTopAdapter?.list?.get(idx)
            if (type == 0) {
                setCropView(gallery!!)
            } else {
                select(gallery!!, false)
            }
        }
        binding.rvTop.visibility = View.GONE
    }


    private fun initGalleryList() {
        val recyclerView = binding.rvContent
        recyclerView.addOnItemTouchListener(object : OnItemTouchListener {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                val action = e.action
                when (action) {
                    MotionEvent.ACTION_MOVE -> rv.parent.requestDisallowInterceptTouchEvent(true)
                }
                return false
            }

            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}
            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
        })

        gallaryAdapter = GallerySelectAdapter(10)
        recyclerView.adapter = gallaryAdapter
        gallaryAdapter?.listener = Unit@{ idx ->

            val selectedImages = selectGallaryList
            val context = binding.root.context
            var totalCnt = selectedImages.size
            val item = gallaryAdapter!!.list.get(idx)

            if (item.isIs_select) {
                totalCnt -= 1
            } else {
                totalCnt += 1
            }

            if (totalCnt > gallaryAdapter!!.selectMax) {
                CommonUtil.showToast(
                    context,
                    String.format(
                        context.getString(R.string.msg_photo_count_limit),
                        gallaryAdapter!!.selectMax
                    )
                )
                return@Unit
            }

            if (item.isIs_select) {
                select(item, false)
            } else {
                select(item, true)
            }
        }
    }


    private fun select(item: Gallary, select: Boolean) {
        var showGallery = item
        if (!select) {
            var selectedNum = 0
            val images = gallaryAdapter!!.list

            for (i in 0 until images.size) {
                if (images[i].isIs_select && images[i].path.equals(item.path)) {
                    selectedNum = images[i].num
                    images[i].num = 0
                    images[i].isIs_select = false
                    break
                }
            }

            for (i in 0 until images.size) {
                if (images[i].isIs_select && images[i].num > selectedNum) {
                    images[i].num -= 1
                }
            }

            for (gallery in selectGallaryList) {
                if (gallery.path.equals(item.path)) {
                    selectGallaryList.remove(gallery)
                    break
                }
            }

            if (selectGallaryList.size > 0) {
                showGallery = selectGallaryList[selectGallaryList.size - 1]
            }
        } else {
            val selectedImages = selectGallaryList
            item.isIs_select = true
            item.num = selectedImages.size + 1
            selectGallaryList.add(item)
        }

        gallaryAdapter?.notifyDataSetChanged()
        gallaryTopAdapter?.list?.clear()
        gallaryTopAdapter?.list?.addAll(selectGallaryList)
        gallaryTopAdapter?.notifyDataSetChanged()

        refreshTopView()
        setCropView(showGallery)
    }

    private fun refreshTopView() {
        if (selectGallaryList.count() > 0) {
            binding.rvTop.visibility = View.VISIBLE
            binding.tvSelect.setText(getString(R.string.gallery_select) + "(${selectGallaryList.count()})")
        } else {
            binding.rvTop.visibility = View.GONE
            binding.tvSelect.setText(getString(R.string.gallery_select))
        }
    }

    private fun setCropRatio(cropRatio: Float) {
        if (cropRatio == 1.0f) {
            binding.tvCrop.setText("1:1")
            binding.ivPhoto1.visibility = View.GONE
            binding.ivPhoto2.visibility = View.VISIBLE
        } else {
            binding.tvCrop.setText("4:3")
            binding.ivPhoto1.visibility = View.VISIBLE
            binding.ivPhoto2.visibility = View.GONE
        }
    }

    private fun setCropView(gallery: Gallary) {
        if (gallery == null) {
            return
        }
        setImageUrl(binding.ivPhoto1, gallery.imageUri.toString())
        setImageUrl(binding.ivPhoto2, gallery.imageUri.toString())
    }

    private fun loadGallery() {
        showProgress()

        imageLoader.abortLoadImages()
        imageLoader.loadDeviceImages(
            true,
            false,
            false,
            arrayListOf(),
            object : ImageLoaderListener {
                override fun onFailed(throwable: Throwable?) {
                    this@GallerySelectCropActivity.runOnUiThread {
                        hideProgress()

                        CommonUtil.showToast(
                            this@GallerySelectCropActivity,
                            R.string.gallery_cannot_load
                        )
                    }
                }

                override fun onImageLoaded(
                    images: MutableList<Gallary>?,
                    folders: MutableList<Folder>?,
                ) {
                    folderList.clear()

                    // 전체 폴더 추가
                    val allFolder = Folder(getString(R.string.all))
                    if (images != null) {
                        allFolder.images.addAll(images)
                        gallaryAdapter?.list?.addAll(images)
                    }
                    folderList.add(allFolder)

                    // 매 폴더별 이미지 추가
                    if (folders != null) {
                        folderList.addAll(folders)
                    }

                    this@GallerySelectCropActivity.runOnUiThread {
                        // 상단 선택이미지 넣기, 임시이미지는 바꾸어주기
                        for (i in 0 until selectGallaryList.size) {
                            selectGallaryList[i].num = (i + 1)
                            if (selectGallaryList[i].content != null && !selectGallaryList[i].content.isEmpty()) {
                                selectGallaryList[i].path = selectGallaryList[i].content
                            }
                        }
                        gallaryTopAdapter?.list?.clear()
                        gallaryTopAdapter?.list?.addAll(selectGallaryList)
                        gallaryTopAdapter?.notifyDataSetChanged()
                        refreshTopView()

                        // 갤러리중 같은 이미지 잇으면 표시해놓기
                        if (gallaryAdapter!!.list.size > 0 && selectGallaryList.size > 0) {
                            val list = gallaryAdapter!!.list
                            for (i in 0 until selectGallaryList.size) {
                                val selectGallery = selectGallaryList.get(i)

                                for (j in 0 until list.size) {
                                    if (list[j].path.equals(selectGallery.path)) {
                                        list[j].num = (i + 1)
                                        list[j].isIs_select = true
                                    }
                                }
                            }
                            gallaryAdapter?.notifyDataSetChanged()
                            setCropView(selectGallaryList[selectGallaryList.size - 1])
                        }

                        // 상단 Folder타이틀
                        folderAdapter?.dataSource?.clear()
                        folderAdapter?.dataSource?.addAll(folderList)
                        folderAdapter?.notifyDataSetChanged()

                        binding.spFolder.setSelection(0)

                        hideProgress()
                    }
                }
            })
    }
}