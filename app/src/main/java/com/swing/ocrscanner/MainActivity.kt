package com.swing.ocrscanner

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.swing.ocrscanner.databinding.ActivityMainBinding
import com.swing.sdk.OcrScanner
import com.swing.sdk.src.interfaces.ImageCallback

class MainActivity : AppCompatActivity(), ImageCallback {

    private lateinit var ocrScanner: OcrScanner

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ocrScanner = OcrScanner.Builder(this@MainActivity)
            .setImageCallback(this)
            .build()

        binding.textView.setOnClickListener {
            ocrScanner.open()
        }
    }

    override fun onSuccess(path: String) {
        Log.d("MainActivity", "result: $path")
    }

    override fun onFailed(msg: String) {
        TODO("Not yet implemented")
    }
}
