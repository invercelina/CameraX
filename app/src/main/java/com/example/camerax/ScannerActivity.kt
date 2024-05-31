package com.example.camerax

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.camerax.ui.theme.CameraXTheme
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import java.util.concurrent.Executors
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ScannerActivity : ComponentActivity() {

    private lateinit var imageAnalysis: ImageAnalysis
    private lateinit var processCameraProvider: ProcessCameraProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CameraXTheme {
                Surface(onClick = { /*TODO*/ }) {

                }
                CameraPreviewScreen()
            }
        }
    }

    //save
    private fun bindInputAnalyser() {
        val barcodeScanner: BarcodeScanner = BarcodeScanning.getClient(
            BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                .build()
        )
        imageAnalysis = ImageAnalysis.Builder().build()

        val cameraExecutor = Executors.newSingleThreadExecutor()

        imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->

        }


    }


    @Composable
    fun CameraPreviewScreen() {
        val lensFacing = CameraSelector.LENS_FACING_BACK
        val lifecycleOwner = LocalLifecycleOwner.current
        val context = LocalContext.current
        val preview = Preview.Builder().build()
        val previewView = remember {
            PreviewView(context)
        }
        val cameraxSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
        LaunchedEffect(lensFacing) {
            val cameraProvider = context.getCameraProvider()
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(lifecycleOwner, cameraxSelector, preview)
            preview.setSurfaceProvider(previewView.surfaceProvider)
            bindInputAnalyser()
        }
        AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())
    }

    private suspend fun Context.getCameraProvider(): ProcessCameraProvider =
        suspendCoroutine { continuation ->
            ProcessCameraProvider.getInstance(this).also { cameraProvider ->
                cameraProvider.addListener({
                    continuation.resume(
                        cameraProvider.get()
                    )
                    //bind camera preview
                }, ContextCompat.getMainExecutor(this))
            }
        }

    companion object {
        fun startScanner(context: Context, onScan: () -> Unit) {
            Intent(context, ScannerActivity::class.java).also {
                context.startActivity(it)
            }
        }
    }
}