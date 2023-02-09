package com.example.gvalzheimersappspikes.uploadfile

import okhttp3.MediaType
import okhttp3.RequestBody
import java.io.File

class UploadFile(
    private val fileUploadService: FileUploadService,
    private val getMediaType: (String) -> MediaType = ::getMediaTypeFromExtension
) {
    suspend operator fun invoke(filename: String, file: File) {
        val url = fileUploadService.getPreSignedUrl(filename)
        val requestBody = RequestBody.create(getMediaType(filename), file)
        fileUploadService.upload(url, requestBody)
    }
}

fun getMediaTypeFromExtension(extension: String): MediaType {
    return MediaType.get("audio/pcm")
}