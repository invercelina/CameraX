package com.example.camerax

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.camerax.ui.theme.CameraXTheme
import com.google.mlkit.vision.barcode.common.Barcode

class MainActivity : ComponentActivity() {

    private val cameraPermission = android.Manifest.permission.CAMERA

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                startScanner()
            }
        }

    private var barcodeType by mutableStateOf("None")
    private var barcodeContent by mutableStateOf("No Content")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CameraXTheme {
                MainScreen(
                    barcodeType = barcodeType,
                    barcodeContent = barcodeContent,
                    onButtonClick = { requestCameraAndStartScanner() }
                )
            }
        }
    }

    private fun requestCameraAndStartScanner() {
        if (isPermissionGranted(cameraPermission)) {
            startScanner()
        } else {
            requestCameraPermission()
        }
    }

    private fun startScanner() {
        ScannerActivity.startScanner(this) { barcodes ->
            barcodes.forEach { barcode ->
                when (barcode.valueType) {
                    Barcode.TYPE_URL -> {
                        barcodeType = "URL"
                        barcodeContent = barcode.displayValue ?: "No Content"
                    }
                    Barcode.TYPE_CONTACT_INFO -> {
                        barcodeType = "Contact Info"
                        barcodeContent = barcode.displayValue ?: "No Content"
                    }
                    else -> {
                        barcodeType = "Other"
                        barcodeContent = barcode.displayValue ?: "No Content"
                    }
                }
            }
        }
    }

    private fun requestCameraPermission() {
        when {
            shouldShowRequestPermissionRationale(cameraPermission) -> {
                cameraPermissionRequest {
                    openPermissionSetting()
                }
            }
            else -> {
                requestPermissionLauncher.launch(cameraPermission)
            }
        }
    }

    @Composable
    fun MainScreen(barcodeType: String, barcodeContent: String, onButtonClick: () -> Unit) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Column {
                Text(text = "QR Type: $barcodeType")
                Text(text = "QR Content: $barcodeContent")
            }
            Button(onClick = { onButtonClick() }) {
                Text(text = "OPEN SCANNER")
            }
        }
    }
}
