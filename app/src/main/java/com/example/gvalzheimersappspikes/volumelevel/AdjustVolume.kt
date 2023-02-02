package com.example.gvalzheimersappspikes.volumelevel

import android.content.Context
import android.content.Context.AUDIO_SERVICE
import android.media.AudioManager

class AdjustVolume(context: Context) {
    private val audioManager: AudioManager = context.getSystemService(AUDIO_SERVICE) as AudioManager

    fun fixVolume() {
        var currentVolumePercent = getCurrentVolumePercent()
        while (currentVolumePercent < TARGET_VOLUME) {
            audioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI)
            currentVolumePercent = getCurrentVolumePercent()
        }
    }

    private fun getCurrentVolumePercent(): Int {
        val volumeLevel = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        val maxVolumeLevel = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        return (volumeLevel.toFloat() / maxVolumeLevel * MAX_VOLUME).toInt()
    }

    companion object {
        const val TARGET_VOLUME = 80
        const val MAX_VOLUME = 100
    }
}