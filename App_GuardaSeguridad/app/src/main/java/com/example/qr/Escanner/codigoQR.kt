package com.example.qr.Escanner

import android.util.Log
import android.util.Size
import android.view.Surface
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class codigoQR (
    private val fragment: Fragment,
    private val previewView: PreviewView,
    private val onResult: (String) -> Unit,
    private val onError: (String) -> Unit
) {

    private val cameraExecutor = Executors.newSingleThreadExecutor()
    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var isProcessing = false
    private var qrEncontradoYa = false
    private val scanner: BarcodeScanner by lazy {
        BarcodeScanning.getClient()
    }

    fun iniciar() {
        qrEncontradoYa = false
        isProcessing = false
        startCamera()
    }

    @Suppress("DEPRECATION")
    private fun startCamera() {
        val context = fragment.requireContext()

        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({

            cameraProvider = cameraProviderFuture.get()
            val provider = cameraProvider ?: return@addListener

            val rotation = previewView.display?.rotation ?: Surface.ROTATION_0

            val preview = Preview.Builder().setTargetRotation(rotation).build().also { previewCamera ->
                    previewCamera.setSurfaceProvider(previewView.surfaceProvider)
                }

            val imageAnalysis = ImageAnalysis.Builder().setTargetResolution(Size(1280, 720))
                .setTargetRotation(rotation).setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
                scanFrame(imageProxy)
            }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                provider.unbindAll()
               camera = provider.bindToLifecycle( fragment.viewLifecycleOwner, cameraSelector,  preview,imageAnalysis )

                Log.d("QR_SCANNER", "Cámara iniciada correctamente")

                enfocarCentro()

                // Linterna opcional
                // camera?.cameraControl?.enableTorch(true)

            } catch (e: Exception) {
                Log.e("QR_SCANNER", "Error iniciando cámara", e)

                fragment.requireActivity().runOnUiThread {  onError("Error al iniciar cámara:\n${e.message}")  }
                detener()
            }

        }, ContextCompat.getMainExecutor(context))
    }

    @androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
    private fun scanFrame(imageProxy: ImageProxy) {

        if (qrEncontradoYa) { imageProxy.close();    return }

        if (isProcessing) { imageProxy.close();  return  }

        val mediaImage = imageProxy.image

        if (mediaImage == null) { imageProxy.close(); return }

        isProcessing = true

        val image = InputImage.fromMediaImage(   mediaImage, imageProxy.imageInfo.rotationDegrees )

        var resultadoFinal: String? = null
        var errorFinal: String? = null

        scanner.process(image).addOnSuccessListener { barcodes ->

                if (barcodes.isNotEmpty()) {
                    val barcode = barcodes.first()
                    val resultado = barcode.rawValue ?: barcode.displayValue

                    if (!resultado.isNullOrEmpty()) {
                        qrEncontradoYa = true
                        resultadoFinal = resultado

                        Log.d("QR_SCANNER", "QR detectado: $resultado")
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e("QR_SCANNER", "Error leyendo QR", e)
                errorFinal = e.message ?: "Error desconocido leyendo QR"
            }
            .addOnCompleteListener {

                imageProxy.close()
                isProcessing = false

                if (resultadoFinal != null) {
                    fragment.requireActivity().runOnUiThread {
                        detener()
                        onResult(resultadoFinal!!)
                    }
                }

                if (errorFinal != null) {
                    fragment.requireActivity().runOnUiThread {
                        detener()
                        onError(errorFinal!!)
                    }
                }
            }
    }

    private fun enfocarCentro() {
        val cam = camera ?: return

        previewView.post {
            val factory = previewView.meteringPointFactory

            val puntoCentro = factory.createPoint(
                previewView.width / 2f,
                previewView.height / 2f
            )

            val action = FocusMeteringAction.Builder(
                puntoCentro,
                FocusMeteringAction.FLAG_AF or FocusMeteringAction.FLAG_AE
            )
                .setAutoCancelDuration(3, TimeUnit.SECONDS)
                .build()

            cam.cameraControl.startFocusAndMetering(action)

            Log.d("QR_SCANNER", "Enfoque aplicado al centro")
        }
    }

    fun detener() {
        try {
            camera?.cameraControl?.enableTorch(false)
            cameraProvider?.unbindAll()
            camera = null

            Log.d("QR_SCANNER", "Cámara detenida")

        } catch (e: Exception) {
            Log.e("QR_SCANNER", "Error deteniendo cámara", e)
        }
    }

    fun liberar() {
        detener()
        cameraExecutor.shutdown()
        scanner.close()
    }
}