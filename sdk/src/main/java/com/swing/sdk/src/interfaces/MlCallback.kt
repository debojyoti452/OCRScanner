package com.swing.sdk.src.interfaces

import androidx.camera.core.ImageAnalysis
import com.swing.sdk.src.base.GraphicOverlay
import com.swing.sdk.src.enums.VisionType

interface MlCallback {
    fun onSelectAnalyser(analyser: VisionType): ImageAnalysis.Analyzer

    fun setGraphicOverlay(overlay: GraphicOverlay)
}
