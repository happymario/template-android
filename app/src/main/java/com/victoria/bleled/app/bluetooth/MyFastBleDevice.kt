package com.victoria.bleled.app.bluetooth

import android.app.Application
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.content.Context
import android.util.Log
import com.clj.fastble.BleManager
import com.clj.fastble.callback.BleGattCallback
import com.clj.fastble.callback.BleNotifyCallback
import com.clj.fastble.callback.BleWriteCallback
import com.clj.fastble.data.BleDevice
import com.clj.fastble.exception.BleException
import com.victoria.bleled.util.CommonUtil
import timber.log.Timber
import kotlin.experimental.xor

class MyFastBleDevice(private val mContext: Context, private val mBleDevice: BleDevice?) {
    private var mBleGattProfile: BluetoothGatt? = null
    private var mListener: Listener? = null
    var isConnecting = false
        private set
    val isMyDevice: Boolean
        get() {
            val name = mBleDevice!!.name
            return name != null && name.toLowerCase()
                .contains(SCAN_IDENTIFIER.toLowerCase())
        }

    var sentText: String = ""
    var noNeedErrorShow: Boolean = false

    fun setListner(listner: Listener?) {
        mListener = listner
    }

    // service UUID, readUUId, writeUUId
    val uuids: Array<String?>
        get() {
            val ret = arrayOfNulls<String>(3)
            if (mBleGattProfile == null) {
                return ret
            }
            var uuid = ""
            for (service in mBleGattProfile!!.services) {
                uuid = service.uuid.toString()
                if (uuid.toLowerCase().substring(
                        0,
                        8
                    ).contains(PRIMARY_UUID)
                ) {
                    for (characteristic in service.characteristics) {
                        val charaProp = characteristic.properties
                        if (charaProp and BluetoothGattCharacteristic.PROPERTY_NOTIFY > 0 && characteristic.uuid.toString()
                                .substring(
                                    0,
                                    8
                                ).contains(READ_UUID)
                        ) {
                            ret[READ_UUID_IDX] =
                                characteristic.uuid.toString()
                        }
                        if (charaProp and BluetoothGattCharacteristic.PROPERTY_WRITE > 0 && characteristic.uuid.toString()
                                .substring(
                                    0,
                                    8
                                ).contains(WRITE_UUID)
                        ) {
                            ret[WRITE_UUID_IDX] =
                                characteristic.uuid.toString()
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
                bleDevice: BleDevice,
                exception: BleException
            ) {
                CommonUtil.showToast(mContext, exception.description)
                isConnecting = false
                if (mListener != null) {
                    mListener!!.onDisconnected()
                }
            }

            override fun onConnectSuccess(
                bleDevice: BleDevice,
                gatt: BluetoothGatt,
                status: Int
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
                status: Int
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

    fun sendText(top: String, bottom: String) {
        var strTop = top
        var strBottom = bottom
        if (top.length < bottom.length) {
            val length = (bottom.length - top.length)
            for (i in 0 until length) {
                strTop = strTop.plus(" ")
            }
        } else {
            val length = (top.length - bottom.length)
            for (i in 0 until length) {
                strBottom = strBottom.plus(" ")
            }
        }

        Timber.d("SendText=>%s", (strTop + strBottom))

        val cmd = Command((strTop + strBottom))
        val resData = ResData(cmd)
        writeData(resData.toByteArray())

        Timber.d("SendedText=>%s", resData.toByteArray().toString())

        sentText = (strTop + strBottom)
    }

    fun retry() {
        if (sentText.isEmpty()) {
            return
        }

        noNeedErrorShow = true
        val cmd = Command(sentText)
        val resData = ResData(cmd)
        writeData(resData.toByteArray())
    }

    val isConnected: Boolean
        get() = if (mBleDevice != null && BleManager.getInstance()
                .isConnected(mBleDevice) == true
        ) {
            true
        } else false

    private fun writeData(data: ByteArray) {
        val uuids = uuids
        if (uuids[SERVICE_UUID_IDX] != null && uuids[WRITE_UUID_IDX] != null
        ) {
            BleManager.getInstance().write(
                mBleDevice,
                uuids[SERVICE_UUID_IDX],
                uuids[WRITE_UUID_IDX],
                data,
                object : BleWriteCallback() {
                    override fun onWriteSuccess(
                        current: Int,
                        total: Int,
                        justWrite: ByteArray
                    ) {
                        if (noNeedErrorShow) {
                            return
                        }
                        CommonUtil.showToast(mContext, "자료가 전송되었습니다.")
                    }

                    override fun onWriteFailure(exception: BleException) {
                        if (noNeedErrorShow) {
                            return
                        }
                        CommonUtil.showToast(mContext, exception.description)
                    }
                })
        }
    }

    private fun initReadListener() {
        val uuids = uuids
        if (uuids[SERVICE_UUID_IDX] != null && uuids[READ_UUID_IDX] != null
        ) {
            BleManager.getInstance().notify(
                mBleDevice,
                uuids[SERVICE_UUID_IDX],
                uuids[READ_UUID_IDX],
                object : BleNotifyCallback() {
                    override fun onNotifySuccess() {
                        Log.d(
                            MyFastBleDevice::class.java.simpleName,
                            "initReadListener"
                        )
                        if (mListener != null) {
                            mListener!!.onReadReady(true)
                        }
                    }

                    override fun onNotifyFailure(exception: BleException) {
                        CommonUtil.showToast(mContext, exception.description)
                        if (mListener != null) {
                            mListener!!.onReadReady(false)
                        }
                    }

                    override fun onCharacteristicChanged(data: ByteArray) {
                        val str2 = String(data)
                        val resData = ResData(Command(str2))

                        Log.d(MyFastBleDevice::class.java.simpleName, str2)

                        if (mListener != null) {
                            mListener!!.onRead(resData)
                        }
                    }
                })
        }
    }

    class ResData {
        var header: Byte = 0x48.toByte()
        var command: Command? = null
        var checksum: Byte = 0

        constructor(com: Command) {
            this.command = com

            val commandBytes = com.toByteArray()
            var xor = header
            for (uuid in commandBytes) {
                xor = xor.xor(uuid)
            }
            checksum = xor
        }

        fun toByteArray(): ByteArray {
            val commandBytes = command?.toByteArray()!!

            var result = ByteArray(2 + commandBytes.size)
            result.set(0, header)

            var idx = 1
            for (c in commandBytes) {
                result.set(idx, c)
                idx++
            }
            result.set(idx, checksum)

            return result
        }
    }

    class Command {
        var len: Byte = 0
        var id: Byte = 0     // 0x00 은 글자 데이터, 0x01 글자가 움직이는 여부, 0x02 글자가 깜빡임 여부
        var data: Array<Byte> = emptyArray()
        var str: String = ""

        constructor(str: String) {
            this.str = str

            val length = str.length / 2
            //val bottomStr = str.substring(length)

            for (idx in 0 until length) {
                // 우, 아래
                val top = str[idx]
                val bottom = str[(idx + str.length / 2)]


                // 행사이 빈공백라인 1렬
                if (idx != (length - 1) && !top.equals(' ') && !bottom.equals(' ')) {
                    data = data.plus(0x00)
                    data = data.plus(0x00)
                }
            }

            len = (2 + data.size).toByte()
        }

        fun toByteArray(): ByteArray {
            var result = ByteArray(2 + data.size)
            result.set(0, len)
            result.set(1, id)

            var idx = 2
            for (c in data) {
                result.set(idx, c)
                idx++
            }

            return result
        }
    }

    data class CharacterData(
        var char: String,
        var data: Array<Byte>
    )

    interface Listener {
        fun onConnected()
        fun onDisconnected()
        fun onReadReady(isReady: Boolean)
        fun onRead(data: ResData?)
    }

    companion object {
        const val PRIMARY_UUID = "fff0"
        const val WRITE_UUID = "fff1"
        const val READ_UUID = "fff2"
        const val SCAN_IDENTIFIER = "JMICROLED"
        const val SERVICE_UUID_IDX = 0
        const val WRITE_UUID_IDX = 1
        const val READ_UUID_IDX = 2

        fun initManager(application: Application?) {
            BleManager.getInstance().init(application)
            BleManager.getInstance()
                .enableLog(true)
                .setReConnectCount(1, 5000)
                .setConnectOverTime(20000).operateTimeout = 5000
        }

        fun disconnectAll() {
            BleManager.getInstance().disconnectAllDevice()
            BleManager.getInstance().destroy()
        }
    }

}
