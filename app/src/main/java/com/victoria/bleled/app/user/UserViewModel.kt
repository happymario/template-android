package com.victoria.bleled.app.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.victoria.bleled.data.DataRepository
import com.victoria.bleled.data.model.ModelUpload
import com.victoria.bleled.data.model.ModelUser
import com.victoria.bleled.data.remote.NetworkObserver
import com.victoria.bleled.data.remote.myservice.BaseResponse
import com.victoria.bleled.util.arch.Event
import com.victoria.bleled.util.arch.base.BaseViewModel
import com.victoria.bleled.util.arch.network.NetworkResult
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class UserViewModel constructor(private val repository: DataRepository) : BaseViewModel() {
    /************************************************************
     *  Variables
     ************************************************************/
    private val _loginCompleteEvent = MutableLiveData<Event<Unit>>()
    val loginCompleteEvent: LiveData<Event<Unit>> = _loginCompleteEvent


    /************************************************************
     *  Public
     ************************************************************/
    fun uploadFile(file: File): LiveData<NetworkResult<ModelUpload>> {
        val requestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
        var multipartBody = MultipartBody.Part.createFormData("uploadfile", file.name, requestBody)

        /*val arrMultipartBody: List<MultipartBody.Part> = ArrayList()
        for (i in filePaths.indices) {
            val file: File = File(filePaths.get(i))
            val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file)
            multipartBody =
                MultipartBody.Part.createFormData("uploadfile[]", file.name, requestFile)
            arrMultipartBody.add(multipartBody)
        }*/

        val api = repository.remoteService.upload(multipartBody)
        return repository.callLiveDataApi(api)
    }


    fun loginUser(id: String, pwd: String) {
        val prefDataSource = repository.prefDataSource;

        val api = repository.remoteService.userLogin(
            id,
            pwd,
            if (prefDataSource.pushToken != null) prefDataSource.pushToken!! else "",
            "android"
        )

        _dataLoading.value = true
        repository.callApi(api, object : NetworkObserver<BaseResponse<ModelUser>>() {
            override fun onChanged(result: NetworkResult<BaseResponse<ModelUser>>) {
                super.onChanged(result)

                if (result != null && result.status.value != NetworkResult.Status.loading) {
                    if (result.status.value == NetworkResult.Status.success) {
                        val user = result.data.data
                        user?.pwd = pwd
                        prefDataSource.user = user
                        _loginCompleteEvent.value = Event(Unit)
                    } else if (result.status.value == NetworkResult.Status.error) {
                        _networkErrorLiveData.value = result
                    }
                    _dataLoading.value = false
                }
            }
        })
    }
}