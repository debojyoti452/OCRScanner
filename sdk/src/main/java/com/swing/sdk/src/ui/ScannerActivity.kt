package com.swing.sdk.src.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.swing.sdk.R
import com.swing.sdk.databinding.ActivityScannerBinding
import com.swing.sdk.src.manager.CameraManager

const val KEY_EVENT_ACTION = "key_event_action"
const val KEY_EVENT_EXTRA = "key_event_extra"
private const val IMMERSIVE_FLAG_TIMEOUT = 100L

class ScannerActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityScannerBinding

    private val cameraManager by lazy {
        CameraManager(activity = this, lifecycleOwner = this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScannerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.startCameraButton.setOnClickListener(this)
        binding.switchCameraButton.setOnClickListener(this)
        binding.flashButton.setOnClickListener(this)

        cameraManager.onOpenCvInit()
        cameraManager.setPermissionList(REQUIRED_PERMISSIONS)
        cameraManager.setRequestPermissionCode(REQUEST_CODE_PERMISSIONS)
        cameraManager.requestPermission()
        cameraManager.setPreviewView(binding.preview)
        cameraManager.setGraphicOverlay(binding.overlay)
        cameraManager.onCameraStart()
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.startCameraButton -> {
                cameraManager.onCameraClick()
//            val intent = Intent()
//            intent.putExtra("result", "Debojyoti Singha")
//            setResult(RESULT_OK, intent)
//            finish()
            }

            R.id.switchCameraButton -> {
                cameraManager.onCameraSwitch()
            }

            R.id.flashButton -> {
                cameraManager.onTurnOnFlash()
            }

            R.id.facialRecognitionButton -> {
                cameraManager.onFacialRecognitionClick()
            }

            R.id.barcodeButton -> {
                cameraManager.onBarcodeClick()
            }

            R.id.imageLabelButton -> {
                cameraManager.onImageLabelClick()
            }

            R.id.textRecognitionButton -> {
                cameraManager.onTextRecognitionClick()
            }

        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
        cameraManager.onCameraPermissionResult(requestCode, permissions, grantResults)
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onResume() {
        super.onResume()
        binding.fragmentContainer.postDelayed({
            hideSystemUI()
        }, IMMERSIVE_FLAG_TIMEOUT)
    }

    override fun onStop() {
        super.onStop()
        cameraManager.onCameraStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraManager.onCameraStop()
    }

    private fun hideSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(
            window,
            binding.fragmentContainer
        ).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    companion object {
        const val REQUEST_CODE_PERMISSIONS = 10
        val REQUIRED_PERMISSIONS = arrayOf(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        )
    }
}
