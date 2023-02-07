package com.example.gvalzheimersappspikes.averagenoiselevel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    fun sendValue(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            GetAverageNoiseLevel().getDbLevel(context)
        }
    }

}
