package com.example.gvalzheimersappspikes.createrecording

import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import app.cash.turbine.test
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File
import java.io.FileOutputStream
import kotlin.math.ceil

class RecorderTest {

    private val audioRecord = mockk<AudioRecord>(relaxed = true)
    private val fileOutputStream = mockk<FileOutputStream>(relaxUnitFun = true)
    private val context = mockk<Context> {
        every { openFileOutput(any(), any()) } returns fileOutputStream
        val filenameSlot = slot<String>()
        every { getFileStreamPath(capture(filenameSlot)) } answers {
            File(filenameSlot.captured)
        }
    }
    private val testSubject = Recorder(context, getAudioRecord = { audioRecord })

    @Test
    fun `can record audio`() {
        every { audioRecord.read(any<ShortArray>(), 0, any()) } answers {
            testSubject.stop()
            1
        }

        testSubject.start()

        verifyOrder {
            audioRecord.bufferSizeInFrames
            audioRecord.startRecording()
            audioRecord.read(any<ShortArray>(), 0, any())
            audioRecord.stop()
            audioRecord.release()
        }
        confirmVerified(audioRecord)
    }

    @Test
    fun `uses correct buffer`() {
        every { audioRecord.bufferSizeInFrames } returns 2
        every { audioRecord.read(any<ShortArray>(), 0, any()) } answers {
            testSubject.stop()
            1
        }

        testSubject.start()

        val bufferSlot = slot<ShortArray>() //[0,0]
        val bufferSizeSlot = slot<Int>() //size of the thing above, which is 2

        verify {
            audioRecord.read(capture(bufferSlot), 0, capture(bufferSizeSlot))
        }

        assertEquals(2, bufferSlot.captured.size) //[0,0] -> 2
        assertEquals(2, bufferSizeSlot.captured) //2
    }

    @Test
    fun `saves buffer contents to a file`() {
        mockAudioRecord(bufferSize = 3, valuesToReturn = listOf(12345, 7569, 13, 2028))

        testSubject.start()

        val listOfBytesToWrite = mutableListOf<ByteArray>()
        verify(exactly = 2) {
            fileOutputStream.write(capture(listOfBytesToWrite))
        }
        assertEquals(listOf<Byte>(57, 48, -111, 29, 13, 0), listOfBytesToWrite[0].toList())
        assertEquals(listOf<Byte>(-20, 7), listOfBytesToWrite[1].toList())
    }

    private fun mockAudioRecord(bufferSize: Int, valuesToReturn: List<Short>) {
        every { audioRecord.bufferSizeInFrames } returns bufferSize
        val lastInvocation = ceil(valuesToReturn.size / bufferSize.toDouble()).toInt()
        var invocation = 0
        val bufferSlot = slot<ShortArray>()
        every { audioRecord.read(capture(bufferSlot), 0, bufferSize) } answers {
            val buffer = bufferSlot.captured
            var shortsRead = bufferSize
            for (i in 0 until bufferSize) {
                val index = invocation * bufferSize + i
                if (index > valuesToReturn.lastIndex) {
                    shortsRead = i
                    break
                }
                buffer[i] = valuesToReturn[index]
            }
            invocation += 1
            if (invocation == lastInvocation) {
                testSubject.stop()
            }
            shortsRead
        }
    }

    @Test
    fun `emits event when stopped`() = runTest {
        every { audioRecord.read(any<ShortArray>(), 0, any()) } answers {
            testSubject.stop()
            1
        }

        testSubject.state.test {
            testSubject.start()
            assertEquals(RecorderState.NotStarted, awaitItem())
            assertEquals(RecorderState.Completed(File("record.pcm")), awaitItem())
        }
    }

    @Test
    fun `creates AudioRecord`() {
        val sampleRate = 44100
        val channelConfig = AudioFormat.CHANNEL_IN_MONO
        val audioFormat = AudioFormat.ENCODING_PCM_16BIT
        val bufferSize = 1024
        mockkConstructor(AudioRecord::class)
        mockkStatic(AudioRecord::getMinBufferSize)
        every {
            AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)
        } returns bufferSize
        every {
            constructedWith<AudioRecord>(
                EqMatcher(MediaRecorder.AudioSource.MIC),
                EqMatcher(sampleRate),
                EqMatcher(channelConfig),
                EqMatcher(audioFormat),
                EqMatcher(bufferSize)
            ).state
        } returns AudioRecord.STATE_INITIALIZED

        val state = createAudioRecord().state

        assertEquals(AudioRecord.STATE_INITIALIZED, state)
        verify {
            constructedWith<AudioRecord>(
                EqMatcher(MediaRecorder.AudioSource.MIC),
                EqMatcher(sampleRate),
                EqMatcher(channelConfig),
                EqMatcher(audioFormat),
                EqMatcher(bufferSize)
            ).state
        }
    }
}
