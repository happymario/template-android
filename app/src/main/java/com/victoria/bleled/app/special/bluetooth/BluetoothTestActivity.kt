package com.victoria.bleled.app.special.bluetooth

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBar
import androidx.recyclerview.widget.LinearLayoutManager
import com.victoria.bleled.R
import com.victoria.bleled.databinding.ActivityTestBluetoothBinding
import com.victoria.bleled.util.arch.base.BaseBindingActivity
import com.victoria.bleled.util.feature.PermissionUtil
import com.victoria.bleled.util.kotlin_ext.getViewModelFactory

@SuppressWarnings("MissingPermission")
class BluetoothTestActivity : BaseBindingActivity<ActivityTestBluetoothBinding>() {
    /************************************************************
     *  Static & Global Members
     ************************************************************/
    companion object {
        private var requiredPermissions = arrayOf(
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_FINE_LOCATION,
        )
    }

    /************************************************************
     *  UI controls & Data members
     ************************************************************/
    private val viewModel by viewModels<BluetoothViewModel> { getViewModelFactory() }
    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            if (PermissionUtil.isPermisionsRevoked(this, requiredPermissions)) {
                PermissionUtil.showPermissionGuide(this, 0)
            } else {
                checkPermissions()
            }
        }

    private val bluetoothSettingLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                scan()
            }
        }

    private var btAdapter: BluetoothAdapter? = null

    // Create a BroadcastReceiver for ACTION_FOUND.
    private val receiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val action: String? = intent.action
            when (action) {
                BluetoothDevice.ACTION_FOUND -> {
                    // Discovery has found a device. Get the BluetoothDevice
                    // object and its info from the Intent.
                    val device: BluetoothDevice? =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    val deviceName = device?.name
                    val deviceHardwareAddress = device?.address // MAC address

                    if (device != null)
                        deviceScanAdapter.addItem(device)
                }
            }
        }
    }

    private val leScanCallback = BluetoothAdapter.LeScanCallback { device, rssi, scanRecord ->
        runOnUiThread {
//            leDeviceListAdapter.addDevice(device)
//            leDeviceListAdapter.notifyDataSetChanged()
        }
    }

    private val deviceScanAdapter by lazy {
        DeviceScanAdapter(onDeviceSelected)
    }

    private val onDeviceSelected: (BluetoothDevice) -> Unit = { device ->
        //ChatServer.setCurrentChatConnection(device)
        // connect
        //device.createRfcommSocketToServiceRecord(UUID.fromString("94:8B:C1:03:15:C3"))
    }

    /************************************************************
     *  Overrides
     ************************************************************/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_bluetooth)

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

        // Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(receiver)
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
    }

    private fun checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            requiredPermissions += arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT
            )
        }
        if (PermissionUtil.hasPermission(this, requiredPermissions)) {
            val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
            this.btAdapter = bluetoothManager.adapter
            if (btAdapter?.isEnabled == false) {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                bluetoothSettingLauncher.launch(enableBtIntent)
            }
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

    private fun scan() {
        if (viewModel.curTabIdx.value == 0) {
            val pairedDevices: Set<BluetoothDevice>? = btAdapter?.bondedDevices
            pairedDevices?.forEach { device ->
                val deviceName = device.name
                val deviceHardwareAddress = device.address // MAC address
                print(deviceName)
            }

            if (pairedDevices != null) {
                deviceScanAdapter.updateItems(pairedDevices!!.toList())
            }

            val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
            registerReceiver(receiver, filter)
        } else {
            btAdapter?.startLeScan(leScanCallback)
        }
    }
}
