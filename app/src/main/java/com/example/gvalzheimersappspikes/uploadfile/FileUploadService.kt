package com.example.gvalzheimersappspikes.uploadfile

import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Query
import retrofit2.http.Url

interface FileUploadService {
    @GET("dev")
    suspend fun getPreSignedUrl(@Query("filename") filename: String): String

    @PUT
    suspend fun upload(@Url url: String, @Body requestBody: RequestBody)
}