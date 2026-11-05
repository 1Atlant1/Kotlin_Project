package com.example.kotlin_project.data.cgm

import android.content.Intent
import android.os.Bundle
import com.example.kotlin_project.domain.model.CgmReading
import com.example.kotlin_project.domain.model.Glucose

object JugglucoIntentParser {

    const val ACTION_XDRIP_BG_ESTIMATE = "com.eveningoutpost.dexdrip.BgEstimate"
    private const val EXTRA_XDRIP_GLUCOSE_MGDL = "com.eveningoutpost.dexdrip.Extras.BgEstimate"
    private const val EXTRA_XDRIP_TIME = "com.eveningoutpost.dexdrip.Extras.Time"

    const val ACTION_GLUCODATA = "glucodata.Minute"
    private const val EXTRA_GLUCODATA_GLUCOSE_MGDL = "glucodata.Minute.mgdl"
    private const val EXTRA_GLUCODATA_TIME = "glucodata.Minute.Time"

    fun parse(intent: Intent, receivedAtMillis: Long = System.currentTimeMillis()): CgmReading? {
        val extras = intent.extras ?: return null
        val (glucoseKey, timeKey) = when (intent.action) {
            ACTION_XDRIP_BG_ESTIMATE -> EXTRA_XDRIP_GLUCOSE_MGDL to EXTRA_XDRIP_TIME
            ACTION_GLUCODATA -> EXTRA_GLUCODATA_GLUCOSE_MGDL to EXTRA_GLUCODATA_TIME
            else -> return null
        }

        val glucoseMgdl = extras.getNumber(glucoseKey)?.toDouble() ?: return null
        val glucoseMmol = Glucose.mgdlToMmol(glucoseMgdl)
        if (glucoseMmol !in Glucose.MIN_MMOL..Glucose.MAX_MMOL) return null

        val timestampMillis = extras.getNumber(timeKey)?.toLong() ?: receivedAtMillis
        return CgmReading(glucoseMmol = glucoseMmol, timestampMillis = timestampMillis)
    }

    @Suppress("DEPRECATION")
    private fun Bundle.getNumber(key: String): Number? = get(key) as? Number
}