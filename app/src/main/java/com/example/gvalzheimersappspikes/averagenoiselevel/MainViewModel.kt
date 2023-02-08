package com.example.gvalzheimersappspikes.averagenoiselevel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gvalzheimersappspikes.createrecording.Recorder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private lateinit var recorder: Recorder

    fun sendValue(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            GetAverageNoiseLevel().getDbLevel(context)
        }
    }

    fun startRecording(context: Context) {
        recorder = Recorder(context)
        viewModelScope.launch(Dispatchers.IO) {
            recorder.start()
        }
    }

    fun stopRecording() {
        recorder.stop()
    }
}
