package com.example.gvalzheimersappspikes.createrecording

import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import io.mockk.*
import org.junit.Assert.assertEquals
import org.junit.Test

class RecorderTest {

    @Test
    fun `can record audio`() {
        val audioRecord = mockk<AudioRecord>(relaxed = true)
        val context = mockk<Context> {
            every { openFileOutput(any(), any()) } returns mockk(relaxUnitFun = true)
        }
        val testSubject = Recorder(context, getAudioRecord = { audioRecord })
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
