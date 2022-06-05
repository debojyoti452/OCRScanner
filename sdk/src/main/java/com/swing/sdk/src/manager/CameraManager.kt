package com.swing.sdk.src.manager

import android.app.Activity
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.swing.sdk.src.base.GraphicOverlay
import com.swing.sdk.src.enums.VisionType
import com.swing.sdk.src.interfaces.CameraCallback
import com.swing.sdk.src.interfaces.MlCallback
import com.swing.sdk.src.ui.painter.TextRecognitionProcessor
import org.opencv.android.OpenCVLoader
import java.util.concurrent.Executors

class CameraManager constructor(
    private val activity: Activity,
    private val lifecycleOwner: LifecycleOwner,
) : CameraCallback, MlCallback {

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

    private val executor by lazy {
        ContextCompat.getMainExecutor(activity)
    }

    private var imageCapture: ImageCapture? = null
    private var preview: PreviewView? = null
    private var cameraModeSelected = CameraSelector.DEFAULT_BACK_CAMERA
    private var camera: Camera? = null
    private var isFlashOn = false
    private var imageAnalyzer: ImageAnalysis? = null
    private var graphicOverlay: GraphicOverlay? = null

    override fun onCameraStart() {
        cameraProviderFuture.addListener(
            {
                val cameraProvider = cameraProviderFuture.get()

                // Preview
                val preview = Preview.Builder()
                    .build()
                    .also {
                        it.setSurfaceProvider(preview?.surfaceProvider)
                    }

                imageAnalyzer = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also {
                        it.setAnalyzer(executor, onSelectAnalyser(VisionType.OCR))
                    }

                try {
                    // Unbind use cases before rebinding
                    cameraProvider?.unbindAll()

                    // Bind use cases to camera
                    camera = cameraProvider?.bindToLifecycle(
                        lifecycleOwner,
                        cameraModeSelected,
                        preview,
                        imageAnalyzer
                    )

                } catch (exc: Exception) {
                    Log.e("TAG", "Use case binding failed", exc)
                }

            }, executor
        )
    }

    override fun onImageSaved(imageUri: Uri) {

    }

    override fun onCameraStop() {
        if (!cameraProviderFuture.isCancelled && !imageExecutor.isTerminated) {
            cameraProviderFuture.cancel(true)
            imageExecutor.shutdown()
        }
    }

    override fun onCameraClick() {

    }

    override fun onCameraError(error: String) {

    }

    override fun onTurnOnFlash() {
        if (camera?.cameraInfo?.hasFlashUnit() == true) {
            isFlashOn = !isFlashOn
            camera?.cameraControl?.enableTorch(isFlashOn)
        }
    }

    override fun onCameraSwitch() {
        Log.d("TAG", "onCameraSwitch")
        cameraModeSelected = if (cameraModeSelected == CameraSelector.DEFAULT_BACK_CAMERA) {
            CameraSelector.DEFAULT_FRONT_CAMERA
        } else {
            CameraSelector.DEFAULT_BACK_CAMERA
        }
        onCameraStart()
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

    }

    override fun onBarcodeClick() {

    }

    override fun onImageLabelClick() {

    }

    override fun onTextRecognitionClick() {

    }

    override fun onSelectAnalyser(analyser: VisionType): ImageAnalysis.Analyzer {
        return when (analyser) {
            VisionType.OCR -> TextRecognitionProcessor(graphicOverlay!!)
            else -> TextRecognitionProcessor(graphicOverlay!!)
        }
    }

    override fun setPreviewView(previewView: PreviewView) {
        this.preview = previewView
    }

    override fun setGraphicOverlay(overlay: GraphicOverlay) {
        this.graphicOverlay = overlay
    }

    companion object {
        val TAG: String = CameraManager::class.java.simpleName
    }
}
