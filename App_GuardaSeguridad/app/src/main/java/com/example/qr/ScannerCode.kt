package com.example.qr

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.qr.R
import androidx.activity.OnBackPressedCallback
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import androidx.camera.view.PreviewView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.qr.Escanner.PermiCamara
import com.example.qr.Escanner.codigoQR
import com.example.qr.conexion_DB.Repositorio.codigoQR_Reposi
import com.example.qr.conexion_DB.Repositorio.estudiante_Reposi
import com.example.qr.conexion_DB.Repositorio.historial_Reposi
import com.example.qr.fecha_Hora_y_TipoComida.FechaHoraUtil
import com.example.qr.fecha_Hora_y_TipoComida.TipoComidaActual
import com.example.qr.guardarDatosTelefono.datosEnMemoria
import com.example.qr.mensajes.mensajes
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.button.MaterialButton
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.launch
import com.example.qr.mensajes.MensajeMovimiento

class ScannerCode : Fragment(R.layout.scanner) {

    private lateinit var contenidoNormal: View
    private lateinit var txtBienvenida: View
    private lateinit var pantallaScanner: View
    private lateinit var previewView: PreviewView
    private lateinit var containerScanner: View
    private lateinit var scannerLine: View
    private lateinit var imgQrPlaceholder: ImageView
    private lateinit var txtResultado: TextView
    private lateinit var btnScan: MaterialButton
    private lateinit var btnCerrarScanner: MaterialButton
    private lateinit var qrScannerManager: codigoQR
    private lateinit var cameraPermissionManager: PermiCamara

    // para el segundo resultado
    private fun Int.dp(): Int {
        return (this * resources.displayMetrics.density).toInt()
    }


    private data class DatosQR(
        val codigoUnico: String,
        val carnet: String
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        cameraPermissionManager = PermiCamara(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        contenidoNormal = view.findViewById(R.id.contenidoNormal)
        pantallaScanner = view.findViewById(R.id.pantallaScanner)
        previewView = view.findViewById(R.id.previewView)

        containerScanner = view.findViewById(R.id.containerScanner)
        scannerLine = view.findViewById(R.id.scanner_line)

        imgQrPlaceholder = view.findViewById(R.id.imgQrPlaceholder)
        txtResultado = view.findViewById(R.id.txtResultado)
        txtBienvenida = view.findViewById(R.id.txtBienvenida)
        btnScan = view.findViewById(R.id.btnScan)
        btnCerrarScanner = view.findViewById(R.id.btnCerrarScanner)


        mostrarNombreBienvenida()

        mostrarPantallaNormal()

        qrScannerManager = codigoQR( fragment = this, previewView = previewView,
            onResult = onResult@{ resultado ->
                if (!isAdded) return@onResult

                mostrarPantallaNormal()
                procesarResultadoQR(resultado)
            },
            onError = onError@{ error ->
                if (!isAdded) return@onError

                mostrarPantallaNormal()

            }
        )

        btnScan.setOnClickListener {

            cameraPermissionManager.verificarOPedirPermiso(
                onGranted = {
                 iniciarEscaneo()
                   /* MensajeMovimiento.mostrar( requireActivity().supportFragmentManager, "Martin Gonzalez", "1234f",
                        "raas", "ENTRADA",null  )*/

                },
                onDenied = {
                    mostrarPantallaNormal()
                    Toast.makeText(requireContext(), "Permiso de cámara denegado.", Toast.LENGTH_LONG).show()

                    btnScan.text = "Escanear Ahora"
                }
            )
        }

        btnCerrarScanner.setOnClickListener {
            cancelarEscaneo()
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (pantallaScanner.visibility == View.VISIBLE) {
                        cancelarEscaneo()
                    } else {
                        isEnabled = false
                        requireActivity().onBackPressedDispatcher.onBackPressed()
                    }
                }
            }
        )
    }

    private fun iniciarEscaneo() {
        mostrarSoloScanner()
        qrScannerManager.iniciar()
    }

    private fun cancelarEscaneo() {
        if (::qrScannerManager.isInitialized) {
            qrScannerManager.detener()
        }

        mostrarPantallaNormal()

        txtResultado.text = "Escaneo cancelado."
        btnScan.text = "Escanear Ahora"
    }

    private fun procesarResultadoQR(resultado: String) {
        val datosQR = dividirDatosQR(resultado)
        val comida = TipoComidaActual.obtenerTipoComida()
        val fechaActual = FechaHoraUtil.obtenerFechaActual()

        if (datosQR == null) {
          //  txtResultado.text = "QR incorrecto:\n$resultado"
            btnScan.text = "Escanear Ahora"

            viewLifecycleOwner.lifecycleScope.launch {
                mensajes.mostrar(requireContext(), layoutInflater,"QR inválido",
                    "Este QR no pertenece al sistema.\nDebe contener carnet y codigoUnico.", mensajes.TipoMensaje.ERROR
                )
            }

            return
        }


        vibrarTelefono()

       // val datosMostrar = "CARNET: ${datosQR.carnet}\n" + "CODIGO: ${datosQR.codigoUnico}"

        btnScan.text = "Escanear otra vez"

        viewLifecycleOwner.lifecycleScope.launch {
            val registrado = try {
                codigoQR_Reposi.verificarYMarcarQR(  datosQR.carnet, datosQR.codigoUnico )

            } catch (e: Exception) {
                e.printStackTrace()
                false
            }

            if (registrado) {
                val datosEstudia = estudiante_Reposi.datoEstudiante(datosQR.carnet)
                if (datosEstudia == null) {
                    mensajes.mostrar(
                        requireContext(),
                        layoutInflater,
                        "Estudiante no encontrado",
                        "No se encontraron los datos del estudiante.",
                        mensajes.TipoMensaje.ERROR
                    )
                    return@launch
                }

                if (datosEstudia.estado_internado == "DENTRO") {
                    val estado = estudiante_Reposi.actualizarEstudiante(datosQR.carnet, "FUERA")

                    if (estado) {
                        val nuevoHistorial = historial_Reposi.nuevoHistorial(
                            datosQR.carnet,
                            FechaHoraUtil.obtenerFechaActual(),
                            FechaHoraUtil.obtenerHoraActual()
                        )

                        if (nuevoHistorial) {
                            viewLifecycleOwner.lifecycleScope.launch {
                                MensajeMovimiento.mostrar(
                                    requireActivity().supportFragmentManager,
                                    datosEstudia.nombres,
                                    datosEstudia.carnet,
                                    datosEstudia.carrera,
                                    "FUERA",
                                    datosEstudia.foto_estudi
                                )
                            }
                        } else {
                            Log.d("Historial", "No se pudo insertar el nuevo Historial")
                        }
                    } else {
                        Log.d("Estudiante", "No se pudo hacer el cambio en en la Tabla estudiante")
                    }

                } else {
                    val estado = estudiante_Reposi.actualizarEstudiante(datosQR.carnet, "DENTRO")

                    if (estado) {
                        val ActualizarHistorial = historial_Reposi.actualizarHistorial(
                            datosQR.carnet,
                            FechaHoraUtil.obtenerFechaActual(),
                            FechaHoraUtil.obtenerHoraActual()
                        )

                        if (ActualizarHistorial) {
                            viewLifecycleOwner.lifecycleScope.launch {
                                MensajeMovimiento.mostrar(
                                    requireActivity().supportFragmentManager,
                                    datosEstudia.nombres,
                                    datosEstudia.carnet,
                                    datosEstudia.carrera,
                                    "ENTRADA",
                                    datosEstudia.foto_estudi
                                )
                            }
                        } else {
                            Log.d("Historial", "No se pudo actualizado el Historial")
                        }
                    } else {
                        Log.d("Estudiante", "No se pudo hacer el cambio en en la Tabla estudiante")
                    }
                }

                // mensajes
                val fotoUrl = estudiante_Reposi.obtenerFotoEstudiante(datosQR.carnet)

                // mensajeFlotante2("Wilber Gonzalez",datosMostrar, comida, fechaActual, fotoUrl)
            } else {
                viewLifecycleOwner.lifecycleScope.launch {
                    mensajes.mostrar(requireContext(), layoutInflater,"Error", "El qr invalido",
                        mensajes.TipoMensaje.ERROR
                    )
                }
            }
        }
    }

    private fun dividirDatosQR(datosQR: String): DatosQR? {
        return try {
            val lineas = datosQR.lines().map { it.trim() }.filter { it.isNotEmpty() }

            var codigoUnico = ""
            var carnet = ""

            for (linea in lineas) {
                val partes = linea.split(":", limit = 2)

                if (partes.size == 2) {
                    val clave = partes[0].trim()
                    val valor = partes[1].trim()

                    when (clave) {
                        "codigoUnico" -> codigoUnico = valor
                        "carnet" -> carnet = valor
                    }
                }
            }

            if (codigoUnico.isEmpty() || carnet.isEmpty()) {
                return null
            }

            DatosQR(
                codigoUnico = codigoUnico,
                carnet = carnet
            )

        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun mostrarPantallaNormal() {
        contenidoNormal.visibility = View.VISIBLE
        pantallaScanner.visibility = View.GONE

        mostrarMenuInferior()

        if (::scannerLine.isInitialized) {
            animarLineaEscaneo(scannerLine)
        }
    }

    private fun mostrarSoloScanner() {
        if (::scannerLine.isInitialized) {
            scannerLine.clearAnimation()
        }

        contenidoNormal.visibility = View.GONE
        pantallaScanner.visibility = View.VISIBLE
        pantallaScanner.bringToFront()

        ocultarMenuInferior()
    }

    private fun animarLineaEscaneo(linea: View) {
        linea.clearAnimation()

        linea.post {
            val distancia = containerScanner.height - linea.height

            val animacion = TranslateAnimation(
                0f, 0f,
                0f, distancia.toFloat()
            )

            animacion.duration = 1800
            animacion.repeatCount = Animation.INFINITE
            animacion.repeatMode = Animation.REVERSE

            linea.startAnimation(animacion)
        }
    }

    private fun mostrarMenuInferior() {
        try {
            val bottomMenu = requireActivity().findViewById<View>(R.id.bottom_nav)
            bottomMenu.visibility = View.VISIBLE
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun ocultarMenuInferior() {
        try {
            val bottomMenu = requireActivity().findViewById<View>(R.id.bottom_nav)
            bottomMenu.visibility = View.GONE
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun vibrarTelefono() {
        val vibrator =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager =
                    requireContext().getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager

                vibratorManager.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                requireContext().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(
                VibrationEffect.createOneShot(
                    500,
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
            )
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(500)
        }
    }
    private fun mostrarNombreBienvenida() {
        val datosMemoria = datosEnMemoria.obtener(requireContext())

        if (datosMemoria != null) {
            // Extraemos solo el primer nombre y el primer apellido
            val primerNombre = datosMemoria.nombre.trim().substringBefore(" ")
            val primerApellido = datosMemoria.apellido.trim().substringBefore(" ")

            val nombreCompleto = "$primerNombre $primerApellido"

            // Asignamos el texto al TextView
            (txtBienvenida as? TextView)?.text = "Bienvenido, $nombreCompleto"

            // Cargar la foto del perfil del guardia
            val imgPerfil = view?.findViewById<ImageView>(R.id.imgPerfilUsuario)
            if (imgPerfil != null) {
                Glide.with(this)
                    .load(datosMemoria.foto)
                    .placeholder(R.drawable.baseline_shield_24)
                    .error(R.drawable.baseline_shield_24)
                    .into(imgPerfil)
            }
        }
    }

    private fun mensajeFlotante2( nombreEst: String, datos: String, comida: String, fecha: String, fotoUrl: String? ) {
        val dialog = BottomSheetDialog(requireContext())
        val vista = layoutInflater.inflate(R.layout.resultado2, null)

        dialog.setContentView(vista)

        val iv2Foto = vista.findViewById<ImageView>(R.id.iv2Foto)
        val txt2Nombre = vista.findViewById<TextView>(R.id.txt2Nombre)
        val txt2Carnet = vista.findViewById<TextView>(R.id.txt2Carnet)
        val tx2Comida = vista.findViewById<TextView>(R.id.tx2Comida)
        val txt2Fecha = vista.findViewById<TextView>(R.id.txt2fecha)
        val bt2Guardar = vista.findViewById<MaterialButton>(R.id.bt2Guardar)

        txt2Nombre.text = nombreEst
        txt2Carnet.text = datos
        tx2Comida.text = comida
        txt2Fecha.text = fecha

        Glide.with(requireContext())
            .load(fotoUrl)
            .placeholder(R.drawable.baseline_person_24)
            .error(R.drawable.baseline_person_24)
            .centerCrop()
            .into(iv2Foto)

        bt2Guardar.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setOnShowListener {
            val bottomSheet = dialog.findViewById<View>(
                com.google.android.material.R.id.design_bottom_sheet
            )

            bottomSheet?.setBackgroundColor(Color.TRANSPARENT)

            bottomSheet?.let { sheet ->
                val behavior = BottomSheetBehavior.from(sheet)

                // Altura inicial: visible solo hasta carnet/código
                behavior.peekHeight = 430.dp()

                // Abre colapsado
                behavior.state = BottomSheetBehavior.STATE_COLLAPSED

                // Permite subir con el dedo
                behavior.isDraggable = true

                // Evita que se cierre arrastrando hacia abajo
                behavior.isHideable = false
            }
        }

        dialog.show()
    }




    override fun onDestroyView() {
        if (::scannerLine.isInitialized) { scannerLine.clearAnimation() }
        if (::qrScannerManager.isInitialized) { qrScannerManager.liberar() }
        super.onDestroyView()
    }
}