package com.victoria.bleled.app.special.bluetooth

import androidx.lifecycle.MutableLiveData
import com.victoria.bleled.data.DataRepository
import com.victoria.bleled.util.arch.base.BaseViewModel

class BluetoothViewModel constructor(private val repository: DataRepository) : BaseViewModel() {

    var curTabIdx: MutableLiveData<Int> = MutableLiveData(0)

    fun setTab(idx: Int) {
        curTabIdx.value = idx
    }
}