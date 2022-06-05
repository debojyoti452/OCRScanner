package com.swing.sdk.src.utils

import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.media.Image
import android.os.Environment
import com.swing.sdk.src.manager.CameraManager
import java.io.File

object FileSaver {
    private val FILE_PATH = File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
        "OCRScanner${File.separator}"
    ).apply {
        if (!exists())
            mkdirs()
    }

    fun makeTempFile(): File = File.createTempFile(Utils.generateImageImage(), ".png", FILE_PATH)

    fun imageToBitmapSaveGallery(image: Image, cameraManager: CameraManager) {
        image.imageToBitmap()
            ?.rotateFlipImage(
                cameraManager.rotation,
                cameraManager.isFrontMode()
            )
            ?.scaleImage(
                cameraManager.getPreviewView(),
                cameraManager.isHorizontalMode()
            )
            ?.let { bitmap ->
                cameraManager.getGraphView().processCanvas.drawBitmap(
                    bitmap,
                    0f,
                    bitmap.getBaseYByView(
                        cameraManager.getGraphView(),
                        cameraManager.isHorizontalMode()
                    ),
                    Paint().apply {
                        xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OVER)
                    })
                cameraManager.getGraphView().processBitmap.saveToGallery(context = cameraManager.getActivity())
            }
    }
}
