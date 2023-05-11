package com.victoria.bleled.app.special.bluetooth

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.*
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBar
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.victoria.bleled.R
import com.victoria.bleled.base.BaseBindingActivity
import com.victoria.bleled.base.extensions.getViewModelFactory
import com.victoria.bleled.databinding.ActivityTestBluetoothBinding
import com.victoria.bleled.util.CommonUtil
import com.victoria.bleled.util.feature.PermissionUtil

private const val SCAN_PERIOD_IN_MILLIS: Long = 10_000

/************************************************************
 * classic: Android6.0에서 connect할때 read message동작함
 * ble: Android galaxy samsung 10이상들에서 read write동작함.
 * 문제점들.
 *  scan할때 버벅거림문제 (thread)
 *  os별로 scan이 안되믄 문제
 *  android 6.0이하 폰을 classic으로 분류못하는 문제
 *  많은 문제들이 있으면 open source fastble를 리용하는것이 가장 빠른길이다.
 *  현재 소스는 test용으로만 리용하자.
 *  https://github.com/android/connectivity-samples 최신 update되면 좋겠는데..
 ************************************************************/
@SuppressWarnings("MissingPermission")
class BluetoothTestActivity : BaseBindingActivity<ActivityTestBluetoothBinding>() {
    /************************************************************
     *  Static & Global Members
     ************************************************************/
    companion object {
        private var requiredPermissions = emptyArray<String>()
    }

    /************************************************************
     *  UI controls & Data members
     ************************************************************/
    private val viewModel by viewModels<BluetoothViewModel> { getViewModelFactory() }

    private val bluetoothSettingLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                scan()
            }
        }
    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            if (PermissionUtil.isPermisionsRevoked(this, requiredPermissions)) {
                PermissionUtil.showPermissionGuide(this, 0)
            } else {
                checkPermissions()
            }
        }

    private var btAdapter: BluetoothAdapter? = null

    // classic Bluetooth
    private var classicReceiver: BroadcastReceiver? = null
    private var classicService: BluetoothClassicService? = null
    private var classicHandler: Handler? = null

    // ble Bluetooth
    private var bluetoothLeScanner: BluetoothLeScanner? = null
    private var bleStopHandler: Handler? = null
    private var bleScanCallback: BleScanCallback? = null

    private var connectedDevice: BluetoothDevice? = null

    private val deviceScanAdapter by lazy {
        DeviceScanAdapter(object : DeviceScanAdapter.Listener {
            override fun onConnect(idx: Int, device: BluetoothDevice) {
                if (device.type == BluetoothDevice.DEVICE_TYPE_CLASSIC) {
                    connectClassic(device)
                } else if (device.type == BluetoothDevice.DEVICE_TYPE_LE || device.type == BluetoothDevice.DEVICE_TYPE_DUAL) {
                    connectBle(device)
                } else {
                    CommonUtil.showNIToast(this@BluetoothTestActivity)
                }
            }

            override fun onWrite(idx: Int, device: BluetoothDevice) {
                if (device.type == BluetoothDevice.DEVICE_TYPE_CLASSIC) {
                    writeClassic(device)
                } else if (device.type == BluetoothDevice.DEVICE_TYPE_LE || device.type == BluetoothDevice.DEVICE_TYPE_DUAL) {
                    writeBle(device)
                } else {
                    CommonUtil.showNIToast(this@BluetoothTestActivity)
                }
            }
        })
    }

    /************************************************************
     *  Overrides
     ************************************************************/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_bluetooth)

        bleStopHandler = Handler(Looper.myLooper()!!)
        initView()
        initViewModel()

        checkPermissions()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.bluetooth, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId === android.R.id.home) {
            finish()
            return true
        }
        if (item.itemId === R.id.menu_scan) {
            scan()
        }
        if (item.itemId === R.id.menu_setting) {
            val intentBluetooth = Intent()
            intentBluetooth.action = Settings.ACTION_BLUETOOTH_SETTINGS
            startActivity(intentBluetooth)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()

        // Make sure we're not doing discovery anymore
        btAdapter?.cancelDiscovery()

        classicService?.stop()
        BleService.stopServer()
    }

    override fun onResume() {
        super.onResume()


        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (classicService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (classicService!!.state == BluetoothClassicService.STATE_NONE) {
                // Start the Bluetooth chat services
                classicService!!.start()
                BleService.startServer(app = application)
            }
        }
    }

    override fun onStart() {
        super.onStart()

        registerBroadcasts()
    }

    override fun onStop() {
        super.onStop()

        unRegisterBroadcasts()
    }

    /************************************************************
     *  Privates
     ************************************************************/
    override fun initView() {
        super.initView()

        setupToolbar()
        binding.rvList.apply {
            layoutManager = LinearLayoutManager(this@BluetoothTestActivity)
            adapter = deviceScanAdapter
        }
    }

    private fun initViewModel() {
        binding.viewmodel = viewModel

        viewModel.curTabIdx.observe(this, Observer { tab ->
            setTabIdx(tab)
        })
    }

    private fun verifyBluetoothCapabilities() {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        btAdapter = bluetoothManager.adapter
        bluetoothLeScanner = btAdapter?.bluetoothLeScanner

        when {
            btAdapter == null ->
                CommonUtil.showToast(this, R.string.not_supported_feature)

            !btAdapter!!.isEnabled -> {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                bluetoothSettingLauncher.launch(enableBtIntent)
            }

            btAdapter!!.isEnabled && !btAdapter!!.isMultipleAdvertisementSupported -> {
                CommonUtil.showToast(this, R.string.msg_bluetooth_ads_not_support)
            }

            btAdapter!!.isEnabled && btAdapter!!.isMultipleAdvertisementSupported -> {
                scan()
                BleService.startServer(app = application)
            }
        }
    }

    private fun checkPermissions() {
        // startReceovery 필수
        requiredPermissions += arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            requiredPermissions += arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_ADVERTISE
            )
        }

        if (PermissionUtil.hasPermission(this, requiredPermissions)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                checkPermissionsBackgroundLocation()
            } else {
                verifyBluetoothCapabilities()
            }
        } else {
            val arrPermission = requiredPermissions
            permissionLauncher.launch(arrPermission)
        }
    }

    private fun checkPermissionsBackgroundLocation() {
        requiredPermissions = arrayOf(
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        )

        if (PermissionUtil.hasPermission(this, requiredPermissions)) {
            verifyBluetoothCapabilities()
        } else {
            val arrPermission = requiredPermissions
            permissionLauncher.launch(arrPermission)
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(findViewById(R.id.toolbar))

        val ab: ActionBar? = supportActionBar
        ab?.title = resources.getStringArray(R.array.arr_special_tech)[0]
        ab?.setDisplayHomeAsUpEnabled(true)
    }

    private fun registerBroadcasts() {
        classicReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val action: String? = intent.action
                when (action) {
                    BluetoothDevice.ACTION_FOUND -> {
                        // Discovery has found a device. Get the BluetoothDevice
                        // object and its info from the Intent.
                        var device: BluetoothDevice? = null
                        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.TIRAMISU) {
                            device = intent.getParcelableExtra(
                                BluetoothDevice.EXTRA_DEVICE,
                                BluetoothDevice::class.java
                            )
                        } else {
                            device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                        }
                        val deviceName = device?.name
                        val deviceHardwareAddress = device?.address // MAC address

                        if (device != null) {
                            deviceScanAdapter.addItem(device)
                        }
                    }

                    BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                        CommonUtil.showToast(
                            this@BluetoothTestActivity,
                            R.string.msg_search_complete
                        )
                    }
                }
            }
        }
        val intentFilter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        registerReceiver(classicReceiver, intentFilter)
    }

    private fun unRegisterBroadcasts() {
        if (classicReceiver != null) {
            unregisterReceiver(classicReceiver)
        }
    }


    private fun scan() {
        if (btAdapter == null || !btAdapter!!.isEnabled) {
            return
        }
        if (viewModel.curTabIdx.value == 0) {
            val pairedDevices: Set<BluetoothDevice>? = btAdapter?.bondedDevices
            pairedDevices?.forEach { device ->
                val deviceName = device.name
                val deviceHardwareAddress = device.address // MAC address
                print(deviceName)
            }

            if (pairedDevices != null) {
                deviceScanAdapter.updateItems(pairedDevices.toList())
            }

            if (btAdapter?.isDiscovering == true) {
                btAdapter?.cancelDiscovery()
            }
            val result = btAdapter?.startDiscovery()
            if (result == false) {
                CommonUtil.showToast(
                    this@BluetoothTestActivity,
                    R.string.msg_not_find_device
                )
            }
        } else {
            val scanFilter = ScanFilter.Builder()
                //.setServiceUuid(ScanFilterService_UUID)
                .build()
            val scanFilterList = listOf<ScanFilter>(scanFilter)
            val scanSettings = ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER).build()
            bleScanCallback = BleScanCallback()
            bleStopHandler?.postDelayed({ stopBleScanning() }, SCAN_PERIOD_IN_MILLIS)
            bluetoothLeScanner?.startScan(scanFilterList, scanSettings, bleScanCallback)
        }
    }

    private fun stopBleScanning() {
        CommonUtil.showToast(
            this@BluetoothTestActivity,
            R.string.msg_search_complete
        )
        bluetoothLeScanner?.stopScan(bleScanCallback)
        bleScanCallback = null
        // update 'last seen' times even though there are no new results
        deviceScanAdapter.notifyDataSetChanged()
    }

    private fun connectClassic(device: BluetoothDevice) {
        // Initialize the BluetoothChatService to perform bluetooth connections
        classicHandler = Handler(Looper.myLooper()!!, object : Handler.Callback {
            override fun handleMessage(msg: Message): Boolean {
                val activity = this@BluetoothTestActivity
                when (msg.what) {
                    BluetoothClassicService.MESSAGE_STATE_CHANGE -> when (msg.arg1) {
                        BluetoothClassicService.STATE_CONNECTED -> {
                            hideProgress()
                            connectedDevice = device
                            CommonUtil.showToast(activity, R.string.msg_connect_device)
                        }

                        BluetoothClassicService.STATE_CONNECTING -> showProgress()
                        BluetoothClassicService.STATE_LISTEN, BluetoothClassicService.STATE_NONE -> {
                            hideProgress()
                            CommonUtil.showToast(
                                activity,
                                R.string.msg_disconnect_device
                            )
                        }
                    }

                    BluetoothClassicService.MESSAGE_WRITE -> {
                        //val writeBuf = msg.obj as ByteArray
                        // construct a string from the buffer
                        //val writeMessage = String(writeBuf)
                        //CommonUtil.showToast(activity, "Me:  $writeMessage")
                    }

                    BluetoothClassicService.MESSAGE_READ -> {
                        val readBuf = msg.obj as ByteArray
                        // construct a string from the valid bytes in the buffer
                        val readMessage = String(readBuf, 0, msg.arg1)
                        CommonUtil.showToast(activity, "Read:  $readMessage")
                    }

                    BluetoothClassicService.MESSAGE_DEVICE_NAME -> {
                        // save the connected device's name
                        CommonUtil.showToast(
                            activity, "Connected to "
                                    + connectedDevice?.name
                        )
                    }

                    BluetoothClassicService.MESSAGE_TOAST -> if (null != activity) {
                        Toast.makeText(
                            activity, msg.data.getString(BluetoothClassicService.TOAST),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                return true
            }
        })
        classicService = BluetoothClassicService(this, classicHandler)
        // Attempt to connect to the device
        var isSecured = true
        val pairedDevices: Set<BluetoothDevice>? = btAdapter?.bondedDevices
        pairedDevices?.forEach { listdevice ->
            val deviceName = listdevice.name
            val deviceHardwareAddress = listdevice.address // MAC address
            if (device.name == deviceName && device.address == deviceHardwareAddress) {
                isSecured = false
            }
        }
        classicService?.connect(device, isSecured)
    }

    private fun connectBle(device: BluetoothDevice) {
        val messageObserver = Observer<String> { message ->
            CommonUtil.showToast(this, "Read:$message")
        }
        val deviceConnectionObserver = Observer<Int> { state ->
            if (BluetoothProfile.STATE_CONNECTED == state) {
                CommonUtil.showToast(
                    this@BluetoothTestActivity,
                    R.string.msg_connect_device
                )
            } else if (BluetoothProfile.STATE_DISCONNECTED == state) {
                CommonUtil.showToast(
                    this@BluetoothTestActivity,
                    R.string.msg_disconnect_device
                )
            }
        }

        BleService.deviceConnection.observe(this, deviceConnectionObserver)
        BleService.messages.observe(this, messageObserver)

        BleService.setCurrentChatConnection(device)
    }

    private fun writeClassic(device: BluetoothDevice) {
        if (classicService == null) {
            return
        }
        // Get the message bytes and tell the BluetoothChatService to write
        val send: ByteArray = "message".toByteArray()
        classicService?.write(send)
    }

    private fun writeBle(device: BluetoothDevice) {
        BleService.sendMessage("message")
    }

    private fun setTabIdx(idx: Int) {
        deviceScanAdapter.updateItems(emptyList())
        scan()
    }

    inner class BleScanCallback : ScanCallback() {
        override fun onBatchScanResults(results: MutableList<ScanResult>?) {
            super.onBatchScanResults(results)

            if (results == null) {
                return
            }
            val deviceList = mutableListOf<BluetoothDevice>()
            for (device in results) {
                deviceList.add(device.device)
            }
            deviceScanAdapter.updateItems(deviceList)
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)

            CommonUtil.showToast(
                this@BluetoothTestActivity,
                R.string.msg_not_find_device
            )
        }

        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)

            if (result != null) {
                deviceScanAdapter.addItem(result.device)
            }
        }
    }
}
