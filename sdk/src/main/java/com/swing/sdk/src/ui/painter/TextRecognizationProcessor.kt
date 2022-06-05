package com.swing.sdk.src.ui.painter

import android.graphics.Rect
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.swing.sdk.src.base.BaseImageAnalyzer
import com.swing.sdk.src.base.GraphicOverlay
import java.io.IOException

class TextRecognitionProcessor constructor(private val view: GraphicOverlay) : BaseImageAnalyzer<Text>() {

    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    override val graphicOverlay: GraphicOverlay
        get() = view

    override fun stop() {
        try {
            recognizer.close()
        } catch (e: IOException) {
            Log.e(TAG, "Exception thrown while trying to close Text Recognition: $e")
        }
    }

    override fun detectInImage(image: InputImage): Task<Text> {
        return recognizer.process(image)
    }

    override fun onSuccess(results: Text, graphicOverlay: GraphicOverlay, rect: Rect) {
        graphicOverlay.clear()
        results.textBlocks.forEach {
            Log.d(TAG, "TextBlock text: ${it.text}")
            val textGraphic = TextRecognitionGraphic(graphicOverlay, it, rect)
            graphicOverlay.add(textGraphic)
        }
        graphicOverlay.postInvalidate()
    }

    override fun onFailure(e: Exception) {
       Log.d(TAG, "Text detection failed $e")
    }

    companion object {
        private const val TAG = "TextProcessor"
    }
}
