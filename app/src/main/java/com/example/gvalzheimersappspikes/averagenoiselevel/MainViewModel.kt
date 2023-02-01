package com.example.gvalzheimersappspikes.averagenoiselevel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    fun sendValue() {
        viewModelScope.launch(Dispatchers.IO) {
            GetAverageNoiseLevel().getDbLevel()
        }
    }

}
