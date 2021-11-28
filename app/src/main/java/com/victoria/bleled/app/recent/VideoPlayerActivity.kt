package com.victoria.bleled.app.recent

import android.app.PictureInPictureParams
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Rational
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.victoria.bleled.R
import com.victoria.bleled.util.CommonUtil
import com.victoria.bleled.util.thirdparty.player.MyVideoPlayer

class VideoPlayerActivity : AppCompatActivity() {
    /************************************************************
     *  Variables
     ************************************************************/
    @RequiresApi(Build.VERSION_CODES.O)
    private val mPipBuilder = PictureInPictureParams.Builder()

    lateinit var videoPlayer: MyVideoPlayer

    /************************************************************
     *  Overrides
     ************************************************************/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)

        setupToolbar()
        setupVideoPlayer()

        videoPlayer.setVideoUrl("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4")
        //videoPlayer.setVideoUrl("https://www.youtube.com/watch?v=p9JYPAcAaRE")
    }

    override fun onPause() {
        super.onPause()
        videoPlayer.setPlayWhenReady(false)
    }

    override fun onDestroy() {
        videoPlayer.release()
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.video_player, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId === android.R.id.home) {
            finish()
            return true
        }
        if (item.itemId === R.id.menu_mode) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                changePicInPicture()
            } else {
                CommonUtil.showToast(this, R.string.not_supported_feature)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /************************************************************
     *  Helpers
     ************************************************************/
    private fun setupToolbar() {
        setSupportActionBar(findViewById(R.id.toolbar))

        val ab: ActionBar? = supportActionBar
        ab?.title = "Player"
        ab?.setDisplayHomeAsUpEnabled(true)
    }


    private fun setupVideoPlayer() {
        videoPlayer = findViewById(R.id.player)

        videoPlayer.setListener { fullscreen ->
            if (!fullscreen) {
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                if (supportActionBar != null) {
                    supportActionBar!!.show()
                }
                val params =
                    videoPlayer.layoutParams
                params.height = (240 * applicationContext.resources
                    .displayMetrics.density).toInt()
                videoPlayer.layoutParams = params
            } else {
                window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
                if (supportActionBar != null) {
                    supportActionBar!!.hide()
                }
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                val metrics = DisplayMetrics()
                windowManager.defaultDisplay.getMetrics(metrics)
                val params =
                    videoPlayer.layoutParams
                params.height = metrics.widthPixels
                videoPlayer.layoutParams = params
            }
        }

        videoPlayer.setOnClickListener {
            videoPlayer.play()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun changePicInPicture() {
        mPipBuilder.setAspectRatio(Rational(100, 200))
        enterPictureInPictureMode(mPipBuilder.build())
    }
}