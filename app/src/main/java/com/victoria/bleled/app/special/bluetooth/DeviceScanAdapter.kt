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
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.victoria.bleled.R
import com.victoria.bleled.databinding.ItemBluetoothDeviceBinding

@SuppressWarnings("MissingPermission")
class DeviceScanAdapter(
    private val listener: Listener,
) : RecyclerView.Adapter<DeviceScanAdapter.DeviceScanViewHolder>() {

    private var items = mutableListOf<BluetoothDevice>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceScanViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        //val view = inflater.inflate(R.layout.item_bluetooth_device, parent, false)
        val binding = ItemBluetoothDeviceBinding.inflate(inflater, parent, false)
        return DeviceScanViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: DeviceScanViewHolder, position: Int) {
        items.getOrNull(position)?.let { result ->
            holder.bind(position, result)
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
        binding: ItemBluetoothDeviceBinding,
        val listener: Listener,
    ) : RecyclerView.ViewHolder(binding.root) {

        private val name = itemView.findViewById<TextView>(R.id.tv_name)
        private val address = itemView.findViewById<TextView>(R.id.tv_uid)
        private var bluetoothDevice: BluetoothDevice? = null
        private var index = 0

        init {
            binding.connect.setOnClickListener {
                listener.onConnect(index, bluetoothDevice!!)
            }
            binding.write.setOnClickListener {
                listener.onWrite(index, bluetoothDevice!!)
            }
        }

        fun bind(idx: Int, device: BluetoothDevice) {
            index = idx
            bluetoothDevice = device
            name.text = device.name
            address.text = device.address
        }
    }

    interface Listener {
        fun onConnect(idx: Int, device: BluetoothDevice)
        fun onWrite(idx: Int, device: BluetoothDevice)
    }
}