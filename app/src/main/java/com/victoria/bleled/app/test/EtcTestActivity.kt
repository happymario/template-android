package com.victoria.bleled.app.test

import android.app.PictureInPictureParams
import android.os.Bundle
import android.util.Rational
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.victoria.bleled.R
import com.victoria.bleled.data.DataRepository
import com.victoria.bleled.util.CommonUtil
import java.util.concurrent.TimeUnit

class EtcTestActivity : AppCompatActivity() {
    private val mPipBuilder = PictureInPictureParams.Builder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_etc)
    }

    fun onExitWithWorkManager(view: View) {
        // 1분이상부터는 제대로 동작함... 왜냐면 workmanager는 시스템상태에 우선도를 부여하기때문이다.
        val workRequest = PeriodicWorkRequestBuilder<SimpleWorker>(1, TimeUnit.MINUTES).build()
        val workManager = WorkManager.getInstance(this)
        workManager.cancelAllWork()

        val prefImple = DataRepository.provideDataRepository(this).prefDataSource
        prefImple.isAutoLogin = true

        workManager?.enqueue(workRequest)

        CommonUtil.showToast(this, "1분후 앱이 다시 켜집니다.")
        finish()
    }

    fun onPicInPicture(view: View) {
        mPipBuilder.setAspectRatio(Rational(100, 200))
        enterPictureInPictureMode(mPipBuilder.build())
    }

    fun onMenu(view: View) {
        val popup = PopupMenu(this, view)
        val inflater: MenuInflater = popup.getMenuInflater()
        inflater.inflate(R.menu.menu_example, popup.getMenu())
        popup.setOnMenuItemClickListener { it: MenuItem? ->
            CommonUtil.showToast(this, R.string.app_finish_message)
            true
        }
        popup.show()
    }
}