package com.example.gvalzheimersappspikes.createrecording

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import io.mockk.*
import org.junit.Assert.assertEquals
import org.junit.Test

class RecorderKtTest {
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