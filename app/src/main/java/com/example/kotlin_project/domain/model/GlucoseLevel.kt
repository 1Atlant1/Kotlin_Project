package com.example.kotlin_project.domain.model

enum class GlucoseLevel {

    NORMAL,

    ELEVATED,

    DANGER;

    companion object {

        const val NORMAL_MIN = 4.0
        const val NORMAL_MAX = 10.0

        const val ELEVATED_MIN = 10.1
        const val ELEVATED_MAX = 13.9

        fun fromMmol(mmol: Double): GlucoseLevel = when {
            mmol < NORMAL_MIN -> DANGER
            mmol <= NORMAL_MAX -> NORMAL
            mmol <= ELEVATED_MAX -> ELEVATED
            else -> DANGER
        }
    }
}