package com.example.kotlin_project.data.cgm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class JugglucoReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val reading = JugglucoIntentParser.parse(intent)
        if (reading == null) {
            Log.w(TAG, "Не удалось разобрать Intent: action=${intent.action}")
            return
        }
        Log.d(TAG, "Получено показание CGM: $reading")

        val pendingResult = goAsync()
        val repository = CgmRepository(context)
        receiverScope.launch {
            try {
                repository.saveReading(reading)
            } finally {
                pendingResult.finish()
            }
        }
    }

    private companion object {
        const val TAG = "JugglucoReceiver"

        val receiverScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    }
}