package com.victoria.bleled.data.repository


import com.victoria.bleled.data.net.mytemplate.IMyTemplateService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MyTemplateRepository @Inject constructor(
    val apiService: IMyTemplateService,
) : BaseRepository() {

}


