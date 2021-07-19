package com.victoria.bleled.app.start

import androidx.lifecycle.LiveData
import com.victoria.bleled.data.DataRepository
import com.victoria.bleled.data.model.ModelAppInfo
import com.victoria.bleled.util.arch.base.BaseViewModel
import com.victoria.bleled.util.arch.network.NetworkResult

class SplashViewModel constructor(val repository: DataRepository) : BaseViewModel() {
    /************************************************************
     *  Public Functions
     ************************************************************/
    fun reqGetAppInfo(): LiveData<NetworkResult<ModelAppInfo>> {
        return repository.getLiveDataApi(repository.remoteService.appInfo("android"))
    }

    /************************************************************
     *  Private Functions
     ************************************************************/

}