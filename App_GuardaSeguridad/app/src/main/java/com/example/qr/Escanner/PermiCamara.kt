package com.example.qr.Escanner

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

class PermiCamara(  private val fragment: Fragment ) {
    private var onPermissionGranted: (() -> Unit)? = null
    private var onPermissionDenied: (() -> Unit)? = null

    // Permiso moderno usando Activity Result API
    private val requestCameraPermissionLauncher = fragment.registerForActivityResult(  ActivityResultContracts.RequestPermission() )
    { isGranted ->

            if (isGranted) { onPermissionGranted?.invoke() }
            else { onPermissionDenied?.invoke() }
    }

    fun verificarOPedirPermiso( onGranted: () -> Unit, onDenied: () -> Unit ) {
        onPermissionGranted = onGranted
        onPermissionDenied = onDenied

        val permisoConcedido = ContextCompat.checkSelfPermission(   fragment.requireContext(),
            Manifest.permission.CAMERA ) == PackageManager.PERMISSION_GRANTED

        if (permisoConcedido) {   onPermissionGranted?.invoke() }
        else { requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA) }
    }
}