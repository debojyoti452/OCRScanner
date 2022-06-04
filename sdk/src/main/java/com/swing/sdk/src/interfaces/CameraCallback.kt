package com.swing.sdk.src.interfaces

import android.net.Uri
import androidx.camera.view.PreviewView

interface CameraCallback {
    fun onCameraStart()

    fun onImageSaved(imageUri: Uri)

    fun onCameraStop()

    fun onCameraClick()

    fun onCameraError(error: String)

    fun onCameraPermissionResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    )

    fun setPreviewView(previewView: PreviewView)

    fun isCameraPermissionGranted(): Boolean

    fun requestPermission()

    fun setPermissionList(permissionList: Array<String>)

    fun getPermissionList(): Array<String>

    fun getRequestPermissionCode(): Int

    fun setRequestPermissionCode(requestCode: Int)

    fun onOpenCvInit()

    fun onFacialRecognitionClick()

    fun onBarcodeClick()

    fun onImageLabelClick()

    fun onTextRecognitionClick()
}