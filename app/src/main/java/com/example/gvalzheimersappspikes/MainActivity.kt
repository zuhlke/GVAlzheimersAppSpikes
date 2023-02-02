package com.example.gvalzheimersappspikes

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import com.example.gvalzheimersappspikes.averagenoiselevel.MainViewModel
import com.example.gvalzheimersappspikes.batterylevel.BatteryLevelCheckerScreen
import com.example.gvalzheimersappspikes.ui.theme.GVAlzheimersAppSpikesTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi

@ExperimentalPermissionsApi

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) MainViewModel().sendValue()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GVAlzheimersAppSpikesTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    BatteryLevelCheckerScreen(this, lifecycle = lifecycle)
                    //requestAudioRecording()
                    //ChangeBrightness().ForceBrightness(0.1f)
                }
            }
        }
    }
    private fun requestAudioRecording() {
        requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
    }

}
