package com.example.gvalzheimersappspikes.uploadfile

import io.mockk.*
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType
import okhttp3.RequestBody
import org.junit.Test
import java.io.File

class UploadFileTest {
    @Test
    fun `can get pre-signed url for upload`() = runTest {
        val file = mockk<File>()
        val fileUploadService = mockk<FileUploadService>(relaxed = true)
        val testSubject = UploadFile(fileUploadService)

        testSubject("helloworld.txt", file)

        coVerify { fileUploadService.getPreSignedUrl("helloworld.txt") }
    }

    @Test
    fun `uploads file to pre-signed url`() = runTest {
        val preSignedUrl = "http://localhost/upload"
        val fileToUpload = mockk<File>()
        val fileUploadService = mockk<FileUploadService> {
            coEvery { getPreSignedUrl("helloworld.pcm") } returns preSignedUrl
            coEvery { upload(preSignedUrl, any()) } returns Unit
        }
        val requestBody = mockRequestBodyCreate(fileToUpload, mediaTypeString = "audio/pcm")
        val testSubject = UploadFile(fileUploadService)

        testSubject("helloworld.pcm", fileToUpload)

        coVerify { fileUploadService.upload(preSignedUrl, requestBody) }
    }

    private fun mockRequestBodyCreate(file: File, mediaTypeString: String): RequestBody {
        mockkStatic("okhttp3.RequestBody")
        val requestBody = mockk<RequestBody>()
        every { RequestBody.create(MediaType.get(mediaTypeString), file) } answers {
            requestBody
        }
        return requestBody
    }

    @Test
    fun `calls getMediaType`() = runTest {
        val fileToUpload = mockk<File>(relaxed = true)
        val fileUploadService = mockk<FileUploadService>(relaxed = true)
        val getMediaType = mockk<(String) -> MediaType>()
        every { getMediaType("audio.pcm") } returns MediaType.get("audio/pcm")
        val testSubject = UploadFile(fileUploadService, getMediaType)

        testSubject("audio.pcm", fileToUpload)

        verify { getMediaType("audio.pcm") }
    }
}