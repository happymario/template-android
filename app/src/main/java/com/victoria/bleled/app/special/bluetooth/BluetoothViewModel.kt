package com.victoria.bleled.app.special.bluetooth

import androidx.lifecycle.MutableLiveData
import com.victoria.bleled.base.BaseViewModel
import com.victoria.bleled.data.repository.MyTemplateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BluetoothViewModel @Inject constructor(private val repository: MyTemplateRepository) :
    BaseViewModel() {

    var curTabIdx: MutableLiveData<Int> = MutableLiveData(0)

    fun setTab(idx: Int) {
        curTabIdx.value = idx
    }
}