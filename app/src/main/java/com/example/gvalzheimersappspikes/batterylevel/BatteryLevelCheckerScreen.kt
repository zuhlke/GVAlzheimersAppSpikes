package com.example.gvalzheimersappspikes.batterylevel

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle

@Composable
fun BatteryLevelCheckerScreen(context: Context, lifecycle: Lifecycle) {
    var batteryLevel by remember { mutableStateOf(-1) }
    var isCharging by remember { mutableStateOf(false) }

    BatteryLevelChecker(context, lifecycle).apply {
        setListener { stats: Stats ->
            batteryLevel = stats.level
            isCharging = stats.charge
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Battery Level: $batteryLevel",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
        if (batteryLevel < 60) {
            Text(text = "Your battery level is lower than 60 percent.")
        }
        if (isCharging) {
            Text(text = "It's charging.")
        }
        Button(onClick = {}, enabled = !(!isCharging && batteryLevel < 60) ) {
            Text(text = "Do something")
        }
    }
}
