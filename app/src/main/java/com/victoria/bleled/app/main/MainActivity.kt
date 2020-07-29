package com.victoria.bleled.app.main

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.util.Log
import android.view.View
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import com.victoria.bleled.R
import com.victoria.bleled.app.bluetooth.LedThreeActivity
import com.victoria.bleled.app.etc.EtcActivity
import com.victoria.bleled.util.CommonUtil
import com.victoria.bleled.util.feature.PermissionUtil
import kotlinx.coroutines.*
import java.lang.Runnable
import java.util.*
import kotlin.coroutines.CoroutineContext


class MainActivity : AppCompatActivity() {

    /************************************************************
     *  Static & Global Members
     ************************************************************/
    private val RC_PERMS = 2001

    /************************************************************
     *  UI controls & Data members
     ************************************************************/
    private val requiredPermissions = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.READ_PHONE_STATE
    )

    private val optionalPermissions = emptyArray<String>()

    /************************************************************
     *  Overrides
     ************************************************************/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d("Kakao", CommonUtil.getKeyHash(this))

        //SHA1: 75:C6:EF:EA:52:59:46:AA:FC:0B:C5:90:25:A6:F3:E6:EF:C2:F2:D1
        var sha1 = byteArrayOf(
            0x85.toByte(),
            0xB9.toByte(),
            0xDB.toByte(),
            0x68.toByte(),
            0x46.toByte(),
            0xE3.toByte(),
            0x58.toByte(),
            0x1F.toByte(),
            0x3E.toByte(),
            0xD7.toByte(),
            0x09.toByte(),
            0xD5.toByte(),
            0x08.toByte(),
            0x38.toByte(),
            0x6D.toByte(),
            0x43.toByte(),
            0xEE.toByte(),
            0x29.toByte(),
            0x34.toByte(),
            0x33.toByte()
        )
        Log.e("keyhash", Base64.encodeToString(sha1, Base64.NO_WRAP))

        testKotlin()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_PERMS) {
            if (PermissionUtil.isPermisionsRevoked(this, getRequiredPermissions())) {
                PermissionUtil.showPermissionGuide(this, requestCode)
            } else {
                checkPermissions()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        @NonNull permissions: Array<String>,
        @NonNull grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == RC_PERMS) {
            if (PermissionUtil.isPermisionsRevoked(this, getRequiredPermissions())) {
                PermissionUtil.showPermissionGuide(this, requestCode)
            } else {
                checkPermissions()
            }
        }
    }

    override fun onPause() {
        super.onPause()
    }


    /************************************************************
     *  Event Handler
     ************************************************************/
    fun onLedThree(view: View) {
        checkPermissions()
    }

    fun onFinish(view: View) {
        finish()
    }

    fun onCamera(view: View) {
        val intent = Intent(this, CameraTestActivity::class.java)
        startActivity(intent)
    }

    fun onGithub(view: View) {
        CommonUtil.showToast(this, R.string.ready_service)
    }

    fun onEtc(view: View) {
        val intent = Intent(this, EtcActivity::class.java)
        startActivity(intent)
    }

    fun onMotion(view: View) {
        val intent = Intent(this, MotionLayoutActivity::class.java)
        startActivity(intent)
    }

    /************************************************************
     *  Helpers
     ************************************************************/
    private fun getPermissions(): Array<String> {
        var arrPermission =
            Array<String>(
                requiredPermissions.size + optionalPermissions.size
            ) { i -> i.toString() }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            arrPermission = Array<String>(
                requiredPermissions.size + optionalPermissions.size + 1
            ) { i -> i.toString() }
        }

        var idx = 0
        for (permisson in requiredPermissions) {
            arrPermission.set(idx, permisson)
            idx++
        }
        for (permisson in optionalPermissions) {
            arrPermission.set(idx, permisson)
            idx++
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            arrPermission.set(idx, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            idx++
        }

        return arrPermission
    }

    private fun getRequiredPermissions(): Array<String> {
        var arrPermission =
            Array<String>(
                requiredPermissions.size
            ) { i -> i.toString() }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            arrPermission = Array<String>(
                requiredPermissions.size + 1
            ) { i -> i.toString() }
        }

        var idx = 0
        for (permisson in requiredPermissions) {
            arrPermission.set(idx, permisson)
            idx++
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            arrPermission.set(idx, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            idx++
        }

        return arrPermission
    }

    private fun checkPermissions() {
        if (PermissionUtil.hasPermission(this, getRequiredPermissions())) {
            val intent = Intent(this, LedThreeActivity::class.java)
            startActivity(intent)

            //CommonUtil.showToast(this, CommonUtil.getDeviceId(this))
        } else {
            PermissionUtil.requestPermission(this, getPermissions(), RC_PERMS)
        }
    }


    private fun testKotlin() {
        for (i in 1..100) {
            Log.d("MainActivity", i.toString())
        } // 닫힌 범위: 100 포함
        for (i in 1 until 100) {
            Log.d("MainActivity", i.toString())
        } // 반만 열린 범위: 100 미포함
        for (x in 2..10 step 2) {
            Log.d("MainActivity", x.toString())
        }
        for (x in 10 downTo 1) {
            Log.d("MainActivity", x.toString())
        }

        val strTest = "Test"
        with(strTest) {
            plus("test")
            Log.d("MainActivity", this)
            substring(IntRange(0, 2))
        }


        //코루틴
        Thread(Runnable {
            for (i in 1..10) {
                Thread.sleep(1000L)
                println("I'm working")
            }
        }).start()

        GlobalScope.launch(Dispatchers.Default) {
            repeat(10) {
                delay(1000L)
                println("I'm working in coroutine")
            }
        }

        // count가 7이 되는 시점에서 해당 suspend 함수가 일시중지 되고 3000ms 후 해당 함수가 재개되었다.
        startPauseableCoroutine()
    }


    /************************************************************
     *  SubClasses
     ************************************************************/
    private val dispatcher = PauseableDispatcher(Handler(Looper.getMainLooper()))
    fun startPauseableCoroutine() {
        val job = GlobalScope.launch(dispatcher) {
            if (this.isActive) {
                suspendFunc()
            }
        }
        GlobalScope.launch {
            println("Start Coroutine...")
            delay(3000L)
            dispatcher.pause()
            println("Pause Coroutine...")
            delay(3000L)
            dispatcher.resume()
            println("Resume Coroutine...")
            delay(3000L)
            job.cancelAndJoin()
            println("Cancel Coroutine...")
        }
    }

    suspend fun suspendFunc() {
        repeat(100) {
            delay(300)
            println("I'm working... count : $it")
        }
    }

    class PauseableDispatcher(private val handler: Handler) : CoroutineDispatcher() {
        private val queue: Queue<Runnable> = LinkedList()
        private var isPaused: Boolean = false

        @Synchronized
        override fun dispatch(context: CoroutineContext, block: Runnable) {
            if (isPaused) {
                queue.add(block)
            } else {
                handler.post(block)
            }
        }

        @Synchronized
        fun pause() {
            isPaused = true
        }

        @Synchronized
        fun resume() {
            isPaused = false
            runQueue()
        }

        private fun runQueue() {
            queue.iterator().let {
                while (it.hasNext()) {
                    val block = it.next()
                    it.remove()
                    handler.post(block)
                }
            }
        }
    }
}
