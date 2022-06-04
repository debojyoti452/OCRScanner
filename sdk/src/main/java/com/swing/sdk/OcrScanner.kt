package com.swing.sdk

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.swing.sdk.src.interfaces.ImageCallback
import com.swing.sdk.src.ui.ScannerActivity

class OcrScanner private constructor(
    val activity: AppCompatActivity,
    val startForResult: ActivityResultLauncher<Intent>,
) {

    private lateinit var ocrScanner: OcrScanner

    open class Builder(private val activity: Activity) {
        private lateinit var imageCallback: ImageCallback

        fun setImageCallback(imageCallback: ImageCallback): Builder {
            this.imageCallback = imageCallback
            return this
        }

        fun build(): OcrScanner {
            if (!::imageCallback.isInitialized) {
                throw IllegalArgumentException("ImageCallback is not set")
            }

            val startForResult =
                (activity as AppCompatActivity).registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                    if (result.resultCode == Activity.RESULT_OK) {
                        val intent = result.data
                        // Handle the Intent
                        intent?.getStringExtra("result")?.let { imageCallback.onSuccess(it) }
                    }
                }

            return OcrScanner(
                activity = activity,
                startForResult = startForResult
            )
        }
    }

    fun open() {
        Log.d("OcrScanner", "start")
        startForResult.launch(Intent(activity, ScannerActivity::class.java))
    }
}

