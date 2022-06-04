package com.swing.sdk.src.utils

import java.text.SimpleDateFormat
import java.util.*

object Utils {
    fun generateImageImage(): String {
        val name = SimpleDateFormat(
            "yyyyMMdd_HHmmss",
            Locale.getDefault()
        ).format(System.currentTimeMillis())

        return "OCR_${name}"
    }
}
