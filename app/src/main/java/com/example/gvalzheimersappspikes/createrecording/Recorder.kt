package com.example.gvalzheimersappspikes.createrecording

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import com.example.gvalzheimersappspikes.averagenoiselevel.PcmToWavUtil
import java.io.FileOutputStream
import java.util.concurrent.atomic.AtomicBoolean


class Recorder {

    private val recording = AtomicBoolean(false)

    private lateinit var recorder: AudioRecord

    fun start(context: Context) {
        recording.set(true)
        Thread {
            startRecording(context)
        }.start()
    }

    fun stop() {
        recording.set(false)
    }

    @SuppressLint("MissingPermission")
    private fun startRecording(context: Context) {
        val sampleRate = 44100
        val channelConfig = AudioFormat.CHANNEL_IN_MONO
        val audioFormat = AudioFormat.ENCODING_PCM_16BIT
        val bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)
        recorder = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate,
            channelConfig,
            audioFormat,
            bufferSize
        )

        val data = ShortArray(bufferSize)
        context.openFileOutput("record.pcm", Context.MODE_PRIVATE).use { fileOutputStream ->
            recorder.startRecording()
            while (recording.get()) {
                val shortsRead = recorder.read(data, 0, bufferSize)
                appendToFile(fileOutputStream, data, shortsRead)
            }
            recorder.stop()
            recorder.release()
        }

        val pcmData = context.openFileInput("record.pcm").readBytes()

        context.openFileOutput("record.wav", Context.MODE_PRIVATE).use {
            it.write(PcmToWavUtil.pcmToWav(pcmData, 1, 44100, 16))
        }
    }


    private fun appendToFile(
        fileOutputStream: FileOutputStream,
        data: ShortArray,
        shortsRead: Int
    ) {
        val pcmData = data.filterIndexed { index, _ -> index < shortsRead }.flatMap { it.toBytes() }
            .toByteArray()
        fileOutputStream.write(pcmData)
//        context.openFileOutput("record.wav", Context.MODE_PRIVATE).use {
//            it.write(PcmToWavUtil.pcmToWav(pcmData, 1, 44100, 16))
//        }
    }


    private fun Short.toBytes(): List<Byte> {
        return listOf(this.toByte(), (this.toInt() shr 8).toByte())
    }

}


