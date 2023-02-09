package com.example.gvalzheimersappspikes.createrecording

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.atomic.AtomicBoolean

class Recorder(
    private val context: Context,
    private val getAudioRecord: () -> AudioRecord = ::createAudioRecord
) {

    private val recording = AtomicBoolean(false)

    private val _state = MutableStateFlow<RecorderState>(RecorderState.NotStarted)
    val state: StateFlow<RecorderState> = _state

    fun start() {
        recording.set(true)
        startRecording()
    }

    fun stop() {
        recording.set(false)
    }

    private fun startRecording() {
        val recorder = getAudioRecord()

        context.openFileOutput("record.pcm", Context.MODE_PRIVATE).use { fileOutputStream ->
            val data = ShortArray(recorder.bufferSizeInFrames)
            recorder.startRecording()
            while (recording.get()) {
                val shortsRead = recorder.read(data, 0, data.size)
                appendToFile(fileOutputStream, data, shortsRead)
            }
            recorder.stop()
            recorder.release()
        }
        _state.value = RecorderState.Completed(context.getFileStreamPath("record.pcm"))

//        val pcmData = context.openFileInput("record.pcm").readBytes()
//
//        context.openFileOutput("record.wav", Context.MODE_PRIVATE).use {
//            it.write(PcmToWavUtil.pcmToWav(pcmData, 1, 44100, 16))
//        }
    }

    private fun appendToFile(
        fileOutputStream: FileOutputStream,
        data: ShortArray,
        shortsRead: Int
    ) {
        val pcmData = data.filterIndexed { index, _ -> index < shortsRead }.flatMap { it.toBytes() }
            .toByteArray()
        fileOutputStream.write(pcmData)
    }

    private fun Short.toBytes(): List<Byte> {
        return listOf(this.toByte(), (this.toInt() shr 8).toByte())
    }
}

sealed class RecorderState {
    object NotStarted : RecorderState()
    data class Completed(val file: File) : RecorderState()
}


@SuppressLint("MissingPermission")
fun createAudioRecord(): AudioRecord {
    val sampleRate = 44100
    val channelConfig = AudioFormat.CHANNEL_IN_MONO
    val audioFormat = AudioFormat.ENCODING_PCM_16BIT
    val bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)
    return AudioRecord(
        MediaRecorder.AudioSource.MIC,
        sampleRate,
        channelConfig,
        audioFormat,
        bufferSize
    )
}