package com.victoria.bleled.app.special.bluetooth

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBar
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.victoria.bleled.R
import com.victoria.bleled.databinding.ActivityTestBluetoothBinding
import com.victoria.bleled.util.CommonUtil
import com.victoria.bleled.util.arch.base.BaseBindingActivity
import com.victoria.bleled.util.feature.PermissionUtil
import com.victoria.bleled.util.kotlin_ext.getViewModelFactory

private const val SCAN_PERIOD_IN_MILLIS: Long = 10_000

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

    // ble Bluetooth
    private var bluetoothLeScanner: BluetoothLeScanner? = null
    private var bleStopHandler: Handler? = null
    private var bleScanCallback: BleScanCallback? = null

    private val deviceScanAdapter by lazy {
        DeviceScanAdapter(onDeviceSelected)
    }

    private val onDeviceSelected: (BluetoothDevice) -> Unit = { device ->
        if (device.type == BluetoothDevice.DEVICE_TYPE_CLASSIC) {
            connectClassic(device)
        } else if (device.type == BluetoothDevice.DEVICE_TYPE_LE) {
            connectBle(device)
        } else {
            CommonUtil.showNIToast(this)
        }
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
    }

    override fun onStart() {
        super.onStart()

        registerBroadcasts()
        scan()
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
                Manifest.permission.BLUETOOTH_CONNECT
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
                            device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE,
                                BluetoothDevice::class.java)
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
                        CommonUtil.showToast(this@BluetoothTestActivity,
                            R.string.msg_search_complete)
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
                CommonUtil.showToast(this@BluetoothTestActivity,
                    R.string.msg_bluetooth_failed_scan)
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
        CommonUtil.showToast(this@BluetoothTestActivity,
            R.string.msg_search_complete)
        bluetoothLeScanner?.stopScan(bleScanCallback)
        bleScanCallback = null
        // update 'last seen' times even though there are no new results
        deviceScanAdapter.notifyDataSetChanged()
    }

    private fun connectClassic(device: BluetoothDevice) {
        //gattClientCallback = GattClientCallback()
        //gattClient = device.connectGatt(app, false, gattClientCallback)
    }

    private fun connectBle(device: BluetoothDevice) {
        //gattClientCallback = GattClientCallback()
        //gattClient = device.connectGatt(app, false, gattClientCallback)
        //ChatServer.setCurrentChatConnection(device)
        // connect
        //device.createRfcommSocketToServiceRecord(UUID.fromString("94:8B:C1:03:15:C3"))
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

            CommonUtil.showToast(this@BluetoothTestActivity,
                R.string.msg_bluetooth_failed_scan)
        }

        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)

            if (result != null) {
                deviceScanAdapter.addItem(result.device)
            }
        }
    }

    private fun setTabIdx(idx: Int) {
        deviceScanAdapter.updateItems(emptyList())
        scan()
    }
}
