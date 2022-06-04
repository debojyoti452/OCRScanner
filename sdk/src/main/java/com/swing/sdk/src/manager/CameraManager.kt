package com.swing.sdk.src.manager

import android.app.Activity
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.swing.sdk.src.interfaces.CameraCallback
import org.opencv.android.OpenCVLoader
import java.util.concurrent.Executors

class CameraManager constructor(
    private val activity: Activity,
    private val lifecycleOwner: LifecycleOwner,
) : CameraCallback {

    private var requestCode: Int = 452
    private var permissionList = arrayOf(
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
    )

    private val imageExecutor by lazy {
        Executors.newSingleThreadExecutor()
    }

    private val cameraProviderFuture by lazy {
        ProcessCameraProvider.getInstance(activity)
    }

    private var imageCapture: ImageCapture? = null

    private val executor by lazy {
        ContextCompat.getMainExecutor(activity)
    }

    private var preview: PreviewView? = null

    override fun onCameraStart() {
        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(preview?.surfaceProvider)
                }

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    lifecycleOwner, cameraSelector, preview
                )

            } catch (exc: Exception) {
                Log.e("TAG", "Use case binding failed", exc)
            }

        }, executor)
    }

    override fun onImageSaved(imageUri: Uri) {
        TODO("Not yet implemented")
    }

    override fun onCameraStop() {
        if (!cameraProviderFuture.isCancelled && !imageExecutor.isTerminated) {
            cameraProviderFuture.cancel(true)
            imageExecutor.shutdown()
        }
    }

    override fun onCameraClick() {
        TODO("Not yet implemented")
    }

    override fun onCameraError(error: String) {
        TODO("Not yet implemented")
    }

    override fun onCameraPermissionResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == getRequestPermissionCode()) {
            if (isCameraPermissionGranted()) {
                onCameraStart()
            } else {
                requestPermissions(
                    activity,
                    getPermissionList(),
                    getRequestPermissionCode()
                )
            }
        }
    }

    override fun isCameraPermissionGranted() = getPermissionList().all {
        ContextCompat.checkSelfPermission(activity, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun getRequestPermissionCode(): Int {
        return requestCode
    }

    override fun setRequestPermissionCode(requestCode: Int) {
        this.requestCode = requestCode
    }

    override fun requestPermission() {
        activity.requestPermissions(permissionList, requestCode)
    }

    override fun setPermissionList(permissionList: Array<String>) {
        this.permissionList = permissionList
    }

    override fun getPermissionList(): Array<String> {
        return permissionList
    }

    override fun onOpenCvInit() {
        if (!OpenCVLoader.initDebug()) {
            throw RuntimeException("OpenCV initialization failed")
        } else {
            println("OpenCV initialized successfully")
        }
    }

    override fun onFacialRecognitionClick() {
        TODO("Not yet implemented")
    }

    override fun onBarcodeClick() {
        TODO("Not yet implemented")
    }

    override fun onImageLabelClick() {
        TODO("Not yet implemented")
    }

    override fun onTextRecognitionClick() {
        TODO("Not yet implemented")
    }

    override fun setPreviewView(previewView: PreviewView) {
        this.preview = previewView
    }

    companion object {
        val TAG: String = CameraManager::class.java.simpleName
    }
}
