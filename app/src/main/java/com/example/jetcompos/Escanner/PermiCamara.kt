package com.example.jetcompos.Escanner

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.core.content.ContextCompat

@Composable
fun PermiCamara(
    context: Context,
    onPermissionGranted: () -> Unit,
    onPermissionDenied: () -> Unit
): () -> Unit {

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->

        if (isGranted) {
            onPermissionGranted()
        } else {
            onPermissionDenied()
        }
    }

    return remember {

        {
            val permisoConcedido =
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED

            if (permisoConcedido) {
                onPermissionGranted()
            } else {
                launcher.launch(Manifest.permission.CAMERA)
            }
        }

    }
}