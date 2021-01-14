package com.victoria.bleled.app.test.bluetooth

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import app.akexorcist.bluetotohspp.library.BluetoothSPP
import app.akexorcist.bluetotohspp.library.BluetoothState
import app.akexorcist.bluetotohspp.library.BluetoothState.REQUEST_ENABLE_BT
import app.akexorcist.bluetotohspp.library.DeviceList
import com.clj.fastble.BleManager
import com.clj.fastble.callback.BleScanCallback
import com.clj.fastble.data.BleDevice
import com.victoria.bleled.R
import com.victoria.bleled.util.CommonUtil


class BluetoothTestActivity : AppCompatActivity() {
    private lateinit var bt: BluetoothSPP // HC05 :  Bluetooth Serial Port Profile이면서 bluetooth 2.0임
    private var fastBleDevice: MyFastBleDevice? = null // FBL80 : Ble 즉 bluetooth 4.0임

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_bluetooth)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        initSPP()

        if (!bt.isBluetoothAvailable) { //블루투스 사용 불가
            CommonUtil.showToast(applicationContext, "Bluetooth is not available")
            finish()
        }

        if (!bt.isBluetoothEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
            return
        }

        startSPP()
        // startScanBle();
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if (resultCode == Activity.RESULT_OK) {
                bt.connect(data)
            }
        } else if (requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                startSPP()
            } else {
                CommonUtil.showToast(applicationContext, "Bluetooth is not available")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        bt.stopService() //블루투스 중지
    }

    fun onScan(view: View) {
        if (bt.serviceState == BluetoothState.STATE_CONNECTED) {
            bt.disconnect()
        } else {
            val intent = Intent(applicationContext, DeviceList::class.java)
            startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE)
        }
    }


    fun onRed(view: View) {
        if (bt.serviceState != BluetoothState.STATE_CONNECTED) {
            return
        }

        if (view.isSelected) {
            bt.send("a", true)
        } else {
            bt.send("d", true)
        }

        view.isSelected = !view.isSelected
    }


    fun onYellow(view: View) {
        if (bt.serviceState != BluetoothState.STATE_CONNECTED) {
            return
        }

        if (view.isSelected) {
            bt.send("b", true)
        } else {
            bt.send("e", true)
        }

        view.isSelected = !view.isSelected
    }


    fun onGreen(view: View) {
        if (bt.serviceState != BluetoothState.STATE_CONNECTED) {
            return
        }

        if (view.isSelected) {
            bt.send("c", true)
        } else {
            bt.send("f", true)
        }

        view.isSelected = !view.isSelected
    }


    private fun initSPP() {
        bt = BluetoothSPP(this) //Initializing
        bt.setOnDataReceivedListener { data, message ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }

        bt.setBluetoothConnectionListener(object :
            BluetoothSPP.BluetoothConnectionListener { //연결됐을 때
            override fun onDeviceConnected(name: String, address: String) {
                Toast.makeText(
                    applicationContext,
                    "Connected to $name\n$address",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onDeviceDisconnected() { //연결해제
                Toast.makeText(applicationContext, "Connection lost", Toast.LENGTH_SHORT).show()
            }

            override fun onDeviceConnectionFailed() { //연결실패
                Toast.makeText(applicationContext, "Unable to connect", Toast.LENGTH_SHORT).show()
            }
        })

    }


    private fun startSPP() {
        bt.setupService()
        bt.startService(false)
    }

    private fun startScanBle() {
        val deviceList = BleManager.getInstance().allConnectedDevice
        if (deviceList != null && deviceList.size > 0) {
            for (i in deviceList.indices) {
                val fastBleDevice =
                    MyFastBleDevice(this, deviceList[i])
                if (fastBleDevice.isMyDevice === true) {
                    initFastBle(fastBleDevice)
                    break
                }
            }
        }

        if (fastBleDevice != null) {
            return
        }

        BleManager.getInstance().scan(object : BleScanCallback() {
            private var searchedMyDevice: MyFastBleDevice? = null
            override fun onScanStarted(success: Boolean) {
                //this@LedThreeActivity.showProgress()
            }

            override fun onLeScan(bleDevice: BleDevice) { //super.onLeScan(bleDevice);
            }

            override fun onScanning(bleDevice: BleDevice) {
                val fastBleDevice = MyFastBleDevice(this@BluetoothTestActivity, bleDevice)
                if (fastBleDevice.isMyDevice) {
                    searchedMyDevice = fastBleDevice
                    BleManager.getInstance().cancelScan()
                }
            }

            override fun onScanFinished(scanResultList: List<BleDevice>) {
                //(this@LedThreeActivity as BaseActivity).hideProgress()
                if (searchedMyDevice == null) {
                    CommonUtil.showToast(this@BluetoothTestActivity, R.string.toast_not_find_device)
                    return
                }
                initFastBle(searchedMyDevice)
            }
        })
    }

    private fun initFastBle(fastBleDevice: MyFastBleDevice?) {
        if (fastBleDevice == null) {
            return
        }
        this.fastBleDevice = fastBleDevice
        this.fastBleDevice?.setListner(object : MyFastBleDevice.Listener {
            override fun onConnected() {
                CommonUtil.showToast(this@BluetoothTestActivity, R.string.toast_connect_device)
            }

            override fun onDisconnected() {

                CommonUtil.showToast(this@BluetoothTestActivity, R.string.toast_disconnect_device)
            }

            override fun onReadReady(isReady: Boolean) {

            }

            override fun onRead(data: MyFastBleDevice.ResData?) {
                val text = data?.command?.str
                if (text != null) {
                    if (text.contains("Retry")) {
                        fastBleDevice?.retry()
                    } else if (text.contains("OK")) {

                    }
                }
            }
        })

        fastBleDevice.connect()
    }

}
