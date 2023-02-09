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

@Suppress("MoveVariableDeclarationIntoWhen")
fun getMediaTypeFromExtension(filename: String): MediaType {
    val extension = filename.substringAfterLast(delimiter = '.', missingDelimiterValue = "")
    val mediaTypeString = when (extension) {
        "json" -> "application/json"
        "pcm" -> "audio/pcm"
        else -> throw IllegalArgumentException("Unknown file extension: $extension")
    }
    return MediaType.get(mediaTypeString)
}