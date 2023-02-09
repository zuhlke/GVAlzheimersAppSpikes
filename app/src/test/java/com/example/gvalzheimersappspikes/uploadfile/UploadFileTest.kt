package com.example.gvalzheimersappspikes.uploadfile

import io.mockk.*
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType
import okhttp3.RequestBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import java.io.File

class UploadFileTest {

    init {
        mockkStatic("okhttp3.RequestBody")
    }

    private val fileToUpload = mockk<File>(relaxed = true)
    private val getMediaType = mockk<(String) -> MediaType>()
    private val fileUploadService = mockk<FileUploadService>()
    private val preSignedUrl = "http://localhost/upload"
    private val requestBody = mockk<RequestBody>()

    @Test
    fun `can get pre-signed url for upload`() = runTest {
        setupMocks("helloworld.txt", MediaType.get("audio/pcm"))
        val testSubject = UploadFile(fileUploadService)

        testSubject("helloworld.txt", fileToUpload)

        coVerify { fileUploadService.getPreSignedUrl("helloworld.txt") }
    }

    @Test
    fun `uploads file to pre-signed url`() = runTest {
        setupMocks("helloworld.pcm", MediaType.get("audio/pcm"))
        val testSubject = UploadFile(fileUploadService)

        testSubject("helloworld.pcm", fileToUpload)

        coVerify { fileUploadService.upload(preSignedUrl, requestBody) }
    }

    @Test
    fun `calls getMediaType and uses it to create request body`() = runTest {
        val mediaType = MediaType.get("audio/pcm")
        setupMocks("audio.pcm", mediaType)
        val testSubject = UploadFile(fileUploadService, getMediaType)

        testSubject("audio.pcm", fileToUpload)

        verify { getMediaType("audio.pcm") }
        verify { RequestBody.create(mediaType, fileToUpload) }
    }

    private fun setupMocks(filename: String, mediaType: MediaType) {
        coEvery { fileUploadService.getPreSignedUrl(filename) } returns preSignedUrl
        coEvery { fileUploadService.upload(preSignedUrl, any()) } returns Unit
        every { getMediaType(filename) } returns mediaType
        every { RequestBody.create(mediaType, fileToUpload) } returns requestBody
    }
}

class GetMediaTypeFromExtensionTest {
    @Test
    fun `return application-json for json files`() {
        val mediaType = getMediaTypeFromExtension("sample.json")

        assertEquals(MediaType.get("application/json"), mediaType)
    }

    @Test
    fun `return audio-pcm for pcm files`() {
        val mediaType = getMediaTypeFromExtension("sample.pcm")

        assertEquals(MediaType.get("audio/pcm"), mediaType)
    }

    @Test
    fun `throws for unknown file type`() {
        val exception = assertThrows(java.lang.IllegalArgumentException::class.java) {
            getMediaTypeFromExtension("sample.txt")
        }
        assertEquals("Unknown file extension: txt", exception.message)
    }
}