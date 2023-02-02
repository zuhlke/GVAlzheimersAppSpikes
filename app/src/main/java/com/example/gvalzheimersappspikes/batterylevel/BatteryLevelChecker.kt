package com.example.gvalzheimersappspikes.batterylevel

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.BatteryState
import android.os.BatteryManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent

data class Stats(val level: Int, val charge: Boolean)

class BatteryLevelChecker(
    private val _context: Context,
    private val _lifecycle: Lifecycle
) : LifecycleEventObserver {

    private var listener: ((Stats) -> Unit)? = null

    init {
        _lifecycle.addObserver(this)
    }

    private val broadcastBatteryInfoListener = object : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {

            intent?.let {
                //remove deprecated stuff

                val level = it.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                val charge =
                    it.getIntExtra(BatteryManager.EXTRA_STATUS, -1) == BatteryState.STATUS_CHARGING
                listener?.invoke(Stats(level, charge))

            }
        }
    }

    fun setListener(listener: ((Stats) -> Unit)?) {
        this.listener = listener
    }

    fun start() {
        _context.registerReceiver(
            broadcastBatteryInfoListener,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        )
    }

    fun stop() {
        _context.unregisterReceiver(broadcastBatteryInfoListener)
    }

    fun destroy() {
        _lifecycle.removeObserver(this)
        listener = null
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_START -> {
                _context.registerReceiver(
                    broadcastBatteryInfoListener,
                    IntentFilter(Intent.ACTION_BATTERY_CHANGED)
                )
            }
            Lifecycle.Event.ON_STOP -> {
                _context.unregisterReceiver(broadcastBatteryInfoListener)
            }
            Lifecycle.Event.ON_DESTROY -> {
                _lifecycle.removeObserver(this)
                listener = null
            }
            else -> {}
        }
    }
}