package com.victoria.bleled.app.user

import androidx.lifecycle.LiveData
import com.victoria.bleled.data.DataRepository
import com.victoria.bleled.data.model.ModelUpload
import com.victoria.bleled.util.arch.base.BaseViewModel
import com.victoria.bleled.util.arch.network.NetworkResult
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class UserViewModel constructor(private val repository: DataRepository) : BaseViewModel() {
    /************************************************************
     *  Variables
     ************************************************************/


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
        return repository.convertBaseResponse(api)
    }


    fun loginUser(id: String, pwd: String) {

    }
}