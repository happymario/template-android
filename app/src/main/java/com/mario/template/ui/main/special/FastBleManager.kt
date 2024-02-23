/***************************************************
---- Init ---
MyApplication.kt --- onCreate
bleManager = FastBleManager(this);
---- BLE Logic ---
startScan.
connectDevice. -> initReadListener
writeData.
 **************************************************/

package com.mario.template.ui.main.special

import android.app.Application
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.content.Context
import com.clj.fastble.BleManager
import com.clj.fastble.callback.BleGattCallback
import com.clj.fastble.callback.BleNotifyCallback
import com.clj.fastble.callback.BleScanCallback
import com.clj.fastble.callback.BleWriteCallback
import com.clj.fastble.data.BleDevice
import com.clj.fastble.exception.BleException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class FastBleManager(var application: Application) {
    companion object {
        const val SCAN_IDENTIFIER = "FBL601_H"
        //const val SCAN_IDENTIFIER = "JMIC"  // Test Device

        const val PRIMARY_UUID = "fff0"
        const val WRITE_UUID = "fff1"
        const val READ_UUID = "fff2"
    }

    init {
        BleManager.getInstance().init(application)
        BleManager.getInstance().enableLog(true).setReConnectCount(1, 5000)
            .setConnectOverTime(20000).operateTimeout = 5000
    }

    fun startScan(context: Context, handler: ScanListener) {
        val deviceList = BleManager.getInstance().allConnectedDevice
        if (deviceList != null && deviceList.size > 0) {
            for (i in deviceList.indices) {
                val fastBleDevice = FastBleDevice(context, deviceList[i])
                if (fastBleDevice.isMyDevice) {
                    handler.onMyDevice(fastBleDevice)
                    break
                }
            }
        }

        BleManager.getInstance().scan(object : BleScanCallback() {
            private var searchedMyDevice: FastBleDevice? = null
            override fun onScanStarted(success: Boolean) {
                handler.onStart()
            }

            override fun onLeScan(bleDevice: BleDevice) { //super.onLeScan(bleDevice);
            }

            override fun onScanning(bleDevice: BleDevice) {
                val fastBleDevice = FastBleDevice(context, bleDevice)
                if (fastBleDevice.isMyDevice) {
                    searchedMyDevice = fastBleDevice
                    BleManager.getInstance().cancelScan()
                }
            }

            override fun onScanFinished(scanResultList: List<BleDevice>) {
                if (searchedMyDevice == null) {
                    for (i in scanResultList.indices) {
                        val fastBleDevice = FastBleDevice(context, scanResultList[i])
                        if (fastBleDevice.isMyDevice) {
                            searchedMyDevice = fastBleDevice
                            break
                        }
                    }
                }

                if (searchedMyDevice != null) {
                    handler.onMyDevice(searchedMyDevice!!)
                }
                handler.onEnd()
            }
        })
    }

    fun enableBluetooth() {
        BleManager.getInstance().enableBluetooth()
    }


    fun stopScan() {
        BleManager.getInstance().cancelScan()
    }

    fun getConnectedDevice(): FastBleDevice? {
        var connectedDevice: FastBleDevice? = null
        val deviceList = BleManager.getInstance().allConnectedDevice
        if (deviceList != null && deviceList.size > 0) {
            for (i in deviceList.indices) {
                val fastBleDevice = FastBleDevice(application, deviceList[i])
                if (fastBleDevice.isMyDevice) {
                    connectedDevice = fastBleDevice
                    break
                }
            }
        }

        return connectedDevice
    }

    fun disconnectAll() {
        BleManager.getInstance().disconnectAllDevice()
        BleManager.getInstance().destroy()
    }


    class FastBleDevice(private val mContext: Context, private val mBleDevice: BleDevice?) {
        companion object {
            const val SERVICE_UUID_IDX = 0
            const val WRITE_UUID_IDX = 1
            const val READ_UUID_IDX = 2
        }

        private var mBleGattProfile: BluetoothGatt? = null

        var isConnecting = false
            private set

        val isConnected: Boolean
            get() = mBleDevice != null && BleManager.getInstance().isConnected(mBleDevice)

        val isMyDevice: Boolean
            get() {
                val name = mBleDevice!!.name
                return name != null && name.lowercase().contains(SCAN_IDENTIFIER.lowercase())
            }

        var mListener: DeviceListener? = null

        var curCommand: String = ""
        var stackData = mutableListOf<Byte>()

        // service UUID, readUUId, writeUUId
        private val uuids: Array<String?>
            get() {
                val ret = arrayOfNulls<String>(3)
                if (mBleGattProfile == null) {
                    return ret
                }
                var uuid = ""
                for (service in mBleGattProfile!!.services) {
                    uuid = service.uuid.toString()
                    if (uuid.lowercase().substring(
                            0, 8
                        ).contains(PRIMARY_UUID)
                    ) {
                        for (characteristic in service.characteristics) {
                            val charaProp = characteristic.properties
                            if (charaProp and BluetoothGattCharacteristic.PROPERTY_NOTIFY > 0 && characteristic.uuid.toString()
                                    .substring(
                                        0, 8
                                    ).contains(READ_UUID)
                            ) {
                                ret[READ_UUID_IDX] = characteristic.uuid.toString()
                            }
                            if (charaProp and BluetoothGattCharacteristic.PROPERTY_WRITE > 0 && characteristic.uuid.toString()
                                    .substring(
                                        0, 8
                                    ).contains(WRITE_UUID)
                            ) {
                                ret[WRITE_UUID_IDX] = characteristic.uuid.toString()
                            }
                        }
                        ret[SERVICE_UUID_IDX] = uuid
                    }
                }
                return ret
            }

        fun connect() {
            isConnecting = true
            BleManager.getInstance().connect(mBleDevice, object : BleGattCallback() {
                override fun onStartConnect() {}
                override fun onConnectFail(
                    bleDevice: BleDevice, exception: BleException,
                ) {
                    Timber.d(exception.toString())

                    isConnecting = false
                    if (mListener != null) {
                        mListener!!.onDisconnected()
                    }
                }

                override fun onConnectSuccess(
                    bleDevice: BleDevice, gatt: BluetoothGatt, status: Int,
                ) {
                    mBleGattProfile = gatt
                    initReadListener()
                    if (mListener != null) {
                        mListener!!.onConnected()
                    }
                }

                override fun onDisConnected(
                    isActiveDisConnected: Boolean,
                    device: BleDevice,
                    gatt: BluetoothGatt,
                    status: Int,
                ) {
                    if (mListener != null) {
                        mListener!!.onDisconnected()
                    }
                }
            })
        }

        fun disconnect() {
            BleManager.getInstance().disconnect(mBleDevice)
        }

        fun sendCommand(command: String, listener: WriteListener? = null) {
            Timber.d("SendText=>%s", command)

            val resData = ReqData(command)

            CoroutineScope(Dispatchers.IO).launch {
                stackData.clear()
                writeData(resData.toByteArray(), listener)
            }

            curCommand = command
        }

        fun retry() {
            if (curCommand.isEmpty()) {
                return
            }

            sendCommand(curCommand)
        }

        private fun writeData(data: ByteArray, listener: WriteListener? = null) {
            val uuids = uuids
            if (uuids[SERVICE_UUID_IDX] != null && uuids[WRITE_UUID_IDX] != null) {
                BleManager.getInstance().write(mBleDevice,
                    uuids[SERVICE_UUID_IDX],
                    uuids[WRITE_UUID_IDX],
                    data,
                    object : BleWriteCallback() {
                        override fun onWriteSuccess(
                            current: Int, total: Int, justWrite: ByteArray,
                        ) {
                            Timber.d("자료가 전송되었습니다.")
                            listener?.onResult(true)
                        }

                        override fun onWriteFailure(exception: BleException) {
                            Timber.d(exception.description)
                            listener?.onResult(false)
                        }
                    })
            }
        }

        private fun initReadListener() {
            val uuids = uuids
            if (uuids[SERVICE_UUID_IDX] != null && uuids[READ_UUID_IDX] != null) {
                BleManager.getInstance().notify(mBleDevice,
                    uuids[SERVICE_UUID_IDX],
                    uuids[READ_UUID_IDX],
                    object : BleNotifyCallback() {
                        override fun onNotifySuccess() {
                            Timber.d("initReadListener")
                            if (mListener != null) {
                                mListener!!.onReadReady(true)
                            }
                        }

                        override fun onNotifyFailure(exception: BleException) {
                            Timber.d(exception.description)
                            if (mListener != null) {
                                mListener!!.onReadReady(false)
                            }
                        }

                        override fun onCharacteristicChanged(data: ByteArray) {
                            Timber.d(String(data))

                            stackData.addAll(data.toTypedArray())

                            val resData = ResData(stackData.toByteArray())

                            if (!resData.isValid()) {
                                return
                            }

                            stackData.clear()
                            if (mListener != null) {
                                mListener!!.onRead(resData)
                            }
                        }
                    })
            }
        }
    }

    // 요청데이터
    class ReqData(command: String) {
        var len: Int = 0
        var data: Array<Byte> = emptyArray()

        init {
            val strCommand = "#$command@"
            data = strCommand.toByteArray().toTypedArray()
            len = data.size
        }

        fun toByteArray(): ByteArray {
            return data.toByteArray()
        }
    }

    // 출력데이터
    class ResData(input: ByteArray) {
        var data: Array<Byte> = emptyArray()

        init {
            data = input.toTypedArray()
        }

        fun toByteArray(): ByteArray {
            val valStr = String(data.toByteArray())
            val leftIdx = valStr.indexOf("#")
            var rightIdx = valStr.indexOf("@")

            if (leftIdx == -1 || rightIdx == -1) {
                return emptyArray<Byte>().toByteArray()
            }

            if (leftIdx > rightIdx) {
                rightIdx = valStr.indexOf("@", leftIdx)
            }

            if (rightIdx == -1) {
                return emptyArray<Byte>().toByteArray()
            }

            val realStr = valStr.substring(leftIdx..rightIdx)
            return realStr.toByteArray()
        }

        // #S[0],hh:mm:ss@
        fun isValid(): Boolean {
            val valStr = String(data.toByteArray())
            val leftIdx = valStr.indexOf("#")
            var rightIdx = valStr.indexOf("@")

            if (leftIdx == -1 || rightIdx == -1) {
                return false
            }

            if (leftIdx > rightIdx) {
                rightIdx = valStr.indexOf("@", leftIdx)
            }
            if (rightIdx == -1) {
                return false
            }
            return true
        }
    }

    interface ScanListener {
        fun onStart()
        fun onMyDevice(device: FastBleDevice)
        fun onEnd()
    }

    interface DeviceListener {
        fun onConnected()
        fun onDisconnected()
        fun onReadReady(isReady: Boolean)
        fun onRead(data: ResData?)
    }

    interface WriteListener {
        fun onResult(success: Boolean)
    }
}