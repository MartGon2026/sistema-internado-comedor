package com.example.jetcompos.Escanner

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.camera.view.PreviewView
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.lifecycleScope
import com.example.jetcompos.conexion_DB.Models.qrComedor
import com.example.jetcompos.conexion_DB.Repositorio.asistencia_Reposi
import com.example.jetcompos.conexion_DB.Repositorio.estudiante_Reposi
import com.example.jetcompos.conexion_DB.Repositorio.qrComedor_Reposi
import com.example.jetcompos.fecha_Hora_y_TipoComida.FechaHoraUtil
import com.example.jetcompos.fecha_Hora_y_TipoComida.TipoComidaActual
import com.example.jetcompos.guardarDatosTelefono.datosEnMemoria
import com.example.jetcompos.mensajeria.MensajeRegistroRepetido
import com.example.jetcompos.mensajeria.Mensajeria
import com.example.jetcompos.mensajeria.ResultadoMensaje
import kotlinx.coroutines.launch

class EscanerQRActivity : ComponentActivity() {

    private lateinit var previewView: PreviewView
    private lateinit var lectorQR: codigoQR
    private var qrDetectado = false
    private var CartnetUsuario = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val datos = datosEnMemoria.obtener(this)

        CartnetUsuario = datos?.carnet ?: "carnet del estudiante"




        // Creamos la vista de la cámara
        previewView = PreviewView(this)

        // La mostramos en toda la pantalla
        setContentView(previewView)

        // Creamos el lector QR
        lectorQR = codigoQR(
            activity = this,
            previewView = previewView,

            onResult = { resultado ->

                if (qrDetectado) return@codigoQR
                // Por el momento solo cerramos la cámara
                println("QR leído: $resultado")
                qrDetectado = true

                lectorQR.liberar()

                lifecycleScope.launch {
/// aqui se hace el proceso de los datos capturados
                   val confirmacion = qrComedor_Reposi.verificarExistencia(resultado)

                    if (confirmacion) {
                        val dato = asistencia_Reposi.ingresarDatos(CartnetUsuario, TipoComidaActual.obtenerTipoComida(), resultado,
                            FechaHoraUtil.obtenerHoraActual(), FechaHoraUtil.obtenerFechaActual(), true )

                        if (dato){
                            Mensajeria.exito("Asistencia Guardada con Exito")
                            Log.d("exitoso", "obtenido: $resultado")
                        } else{
                            val datosEstu  = estudiante_Reposi.obtenerDatosEstudiante(CartnetUsuario)

                           /* MensajeRegistroRepetido.mostrar( datosEstu?.foto_estudi ?:"no tiene", datosEstu ?.nombres ?: "Estudiante",
                                 datosEstu ?.carrera ?:"", TipoComidaActual.obtenerTipoComida() , FechaHoraUtil.obtenerFechaCorta()
                            )*/
                            Mensajeria.exito("Asistencia Guardada con Exito")

                        }

                    } else {
                        Mensajeria.error(" El código QR escaneado no es válido o no tienes una beca alimenticia activa.")
                    }
                    finish()

                }


            },

            onError = { error ->

                println("ERROR: $error")

            }
        )

        // Iniciamos el escáner
        lectorQR.iniciar()
    }

    override fun onDestroy() {
        super.onDestroy()
        lectorQR.liberar()
    }
}