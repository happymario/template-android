/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.victoria.bleled.app.special.bluetooth

import android.bluetooth.BluetoothDevice
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.victoria.bleled.R

@SuppressWarnings("MissingPermission")
class DeviceScanAdapter(
    private val onDeviceSelected: (BluetoothDevice) -> Unit,
) : RecyclerView.Adapter<DeviceScanAdapter.DeviceScanViewHolder>() {

    private var items = mutableListOf<BluetoothDevice>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceScanViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_bluetooth_device, parent, false)
        return DeviceScanViewHolder(view, onDeviceSelected)
    }

    override fun onBindViewHolder(holder: DeviceScanViewHolder, position: Int) {
        items.getOrNull(position)?.let { result ->
            holder.bind(result)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun updateItems(results: List<BluetoothDevice>) {
        items.clear()
        items.addAll(results)
        notifyDataSetChanged()
    }

    fun addItem(item: BluetoothDevice) {
        items.removeAll {
            it.name == item.name && it.address == item.address
        }
        items += item
        notifyDataSetChanged()
    }

    class DeviceScanViewHolder(
        view: View,
        val onDeviceSelected: (BluetoothDevice) -> Unit,
    ) : RecyclerView.ViewHolder(view), View.OnClickListener {

        private val name = itemView.findViewById<TextView>(R.id.tv_name)
        private val address = itemView.findViewById<TextView>(R.id.tv_uid)
        private var bluetoothDevice: BluetoothDevice? = null

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(device: BluetoothDevice) {
            bluetoothDevice = device
            name.text = device.name
            address.text = device.address
        }

        override fun onClick(view: View) {
            bluetoothDevice?.let { device ->
                onDeviceSelected(device)
            }
        }
    }
}