package com.example.gvalzheimersappspikes

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.gvalzheimersappspikes.averagenoiselevel.MainViewModel
import com.example.gvalzheimersappspikes.brightnesslevel.ChangeBrightness
import com.example.gvalzheimersappspikes.createrecording.Recorder
import com.example.gvalzheimersappspikes.ui.theme.GVAlzheimersAppSpikesTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi

@ExperimentalPermissionsApi

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) MainViewModel().sendValue(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GVAlzheimersAppSpikesTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    ///Get Battery Level///
                    //BatteryLevelCheckerScreen(this, lifecycle = lifecycle)

                    ///Get Noise Level///
                    //requestAudioRecording()

                    ///Set Brightness///
                    //ChangeBrightness().ForceBrightness(0.1f)

                    ///Get Volume and get it to 80%///
                    //AdjustVolume().getCurrentVolume(this)
                    //AdjustVolume(this).fixVolume()

                    ///Internet Connectivity Check///
                    //ConnectivityIndicator()

//                    val logcatWorkRequest = OneTimeWorkRequestBuilder<LogcatWorker>()
//                        .setConstraints(
//                            Constraints.Builder()
//                                .setRequiredNetworkType(
//                                    NetworkType.CONNECTED
//                                )
//                                .build()
//                        )
//                        .build()
//
//                    val workManager = WorkManager.getInstance(applicationContext)
//                    workManager.enqueue(logcatWorkRequest)
                    /*val mode = Settings.System.getInt(
                        applicationContext.contentResolver,
                        Settings.System.SCREEN_BRIGHTNESS_MODE
                    )
                    val oldBrightness = Settings.System.getInt(
                        applicationContext.contentResolver,
                        Settings.System.SCREEN_BRIGHTNESS
                    )*/

                    //Log.d("SCReaaa",oldBrightness.toString())

                    //this.setBrightness((255*0.8).toFloat())
                    //ChangeBrightness().ForceBrightness(brightness = 0.8f)
                    //Log.d("SCReaaa",oldBrightness.toString())
                    val recorder = Recorder(applicationContext)
                    RecordingUI(recorder)

                }

            }
        }
    }

    private fun requestAudioRecording() {
        requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
    }

    private fun ComponentActivity.setBrightness(brightness: Float) {
        val layoutParams = window.attributes
        layoutParams.screenBrightness = brightness
        window.attributes = layoutParams
    }

}

@Composable
fun RecordingUI(recorder: Recorder) {
    Column {
        Button(onClick = { recorder.start() }) {
            Text("Start Recording")
        }
        Button(onClick = { recorder.stop() }) {
            Text("Stop Recording")
        }
    }
}
