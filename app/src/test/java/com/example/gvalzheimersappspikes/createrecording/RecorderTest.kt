package com.example.gvalzheimersappspikes.createrecording

import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import io.mockk.*
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class RecorderTest {

    private val audioRecord = mockk<AudioRecord>(relaxed = true)
    private val context = mockk<Context> {
        every { openFileOutput(any(), any()) } returns mockk(relaxUnitFun = true)
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
