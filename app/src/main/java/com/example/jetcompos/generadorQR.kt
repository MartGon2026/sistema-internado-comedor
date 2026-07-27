package com.example.jetcompos

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.delay
import androidx.compose.animation.core.tween
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.jetcompos.conexion_DB.Repositorio.codigoQR_Reposi

import com.example.jetcompos.fecha_Hora_y_TipoComida.FechaHoraUtil
import com.example.jetcompos.fecha_Hora_y_TipoComida.TipoComidaActual
import com.example.jetcompos.guardarDatosTelefono.datosEnMemoria
import kotlinx.coroutines.launch
import java.net.URLEncoder


var tipoComia =""
var Carnet =""
var hora = ""
var fecha =""
@Composable
fun generadorQR(  mostrar: Boolean, alCerrar: () -> Unit ) {

    println("qrRENDER mostrar = $mostrar")
    if (!mostrar) return

    // Reiniciar el tiempo a 20 cada vez que "mostrar" pase a ser true
    var tiempoRestante by remember(mostrar) { mutableStateOf(20) }
    var urlQr by remember { mutableStateOf("") }
    var codigoActual by remember {  mutableStateOf("") }
    var QR_cambio_activo by remember { mutableStateOf(true) }
    val scopeSegundoPlano = rememberCoroutineScope()
    var CartnetUsuario by remember { mutableStateOf("") }

    val context = LocalContext.current

    val datos = remember {    datosEnMemoria.obtener(context)  }

    CartnetUsuario = datos?.carnet ?: "carnet del estudiante"


    darDatosFechaHoraYcomida()
    // Corregido: Escucha los cambios de la variable 'mostrar'
    LaunchedEffect(mostrar) {

        if (mostrar) {
            val caracteres = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
             codigoActual = (1..7).map { caracteres.random() }.joinToString("")

            val user = "alex"


            // guardar QR en la base de dato
            codigoQR_Reposi.ingresarQR(CartnetUsuario,codigoActual,tipoComia,  fecha,  hora )

            val contenidoQR = "codigoUnico: $codigoActual\ncarnet: $CartnetUsuario"


            //se mandara los datos  a la bas de dato


            urlQr = "https://api.qrserver.com/v1/create-qr-code/?size=250x250&data=${URLEncoder.encode(contenidoQR, "UTF-8")}&color=14532d"


            // Bucle del temporizador
            while (tiempoRestante > 0 && QR_cambio_activo) {
                delay(1000L)
                tiempoRestante--

                /// verificar si el qr cambio a true
                val utilizado = codigoQR_Reposi.qrCambio(codigoActual)
                if (utilizado){
                    QR_cambio_activo = false
                    alCerrar()
                    return@LaunchedEffect
                }


            }
            if (QR_cambio_activo) {
                codigoQR_Reposi.eliminarQR(codigoActual)
                alCerrar()
            }
        }


    }


    Box(
        modifier = Modifier.fillMaxSize().background(coloress.PrimaryGreen.copy(alpha = 0.95f)).clickable(enabled = true, onClick = {}), // Bloquear clicks de fondo
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.padding(30.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = coloress.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(25.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text( text = "Código QR", color = coloress.PrimaryGreen,  fontWeight = FontWeight.Bold, fontSize = 18.sp )
                Text( text = "Válido por 20 segundos", color = coloress.TextoSecundario, fontSize = 12.sp, modifier = Modifier.padding(top = 5.dp) )

                AsyncImage(
                    model = urlQr,
                    contentDescription = "QR Code",
                    modifier = Modifier.size(200.dp) .padding(vertical = 15.dp)
                )

                Text( text = "${tiempoRestante}s",  color = coloress.PrimaryGreen, fontWeight = FontWeight.Bold, fontSize = 24.sp )

                Spacer(modifier = Modifier.height(10.dp))

              /// animar la barra para que se mire desbaneciente
                val progresoAnimado by animateFloatAsState(
                    targetValue = tiempoRestante.toFloat() / 20f,
                    animationSpec = tween(
                        durationMillis = 1000
                    ),
                    label = "barraQR"
                )
              // color a la barra mediante desvanece
                val colorBarra = when {
                    tiempoRestante > 10 -> coloress.PrimaryGreen
                    tiempoRestante > 5 -> coloress.Amarillo
                    else -> coloress.rojo
                }

                // Contenedor de la barra de tiempo
                Box(
                    modifier = Modifier.fillMaxWidth().height(8.dp).background(
                            coloress.GrisClaro,
                            RoundedCornerShape(4.dp)
                        )
                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth(progresoAnimado).fillMaxHeight().background(
                                colorBarra,
                                RoundedCornerShape(4.dp)
                            )
                    )
                }

                Spacer(modifier = Modifier.height(25.dp))

                Button(
                    onClick = {
                        scopeSegundoPlano.launch {
                            codigoQR_Reposi.eliminarQR(codigoActual)
                            alCerrar()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = coloress.PrimaryGreen),
                    modifier = Modifier.clip(RoundedCornerShape(20.dp))
                ) {
                    Text(text = "Cerrar", color = coloress.White, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

fun darDatosFechaHoraYcomida() {
    hora = FechaHoraUtil.obtenerHoraActual()
    fecha = FechaHoraUtil.obtenerFechaActual()
    tipoComia = "CENA" //TipoComidaActual.obtenerTipoComida()
    println("$hora\n$fecha\n$tipoComia")

}

@Preview(showBackground = true)
@Composable
fun GeneradorQRPreview() {
    generadorQR(
        mostrar = true,
        alCerrar = {}
    )
}