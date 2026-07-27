package com.example.jetcompos.mensajeria

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage

data class DatosRegistroRepetido(
    val fotoUrl: String,
    val nombre: String,
    val carrera: String,
    val tipoComida: String,
    val fecha: String
)

object MensajeRegistroRepetido {

    var datosActuales by mutableStateOf<DatosRegistroRepetido?>(null)
        private set

    fun mostrar(
        fotoUrl: String,
        nombre: String,
        carrera: String,
        tipoComida: String,
        fecha: String
    ) {
        datosActuales = DatosRegistroRepetido(
            fotoUrl = fotoUrl,
            nombre = nombre,
            carrera = carrera,
            tipoComida = tipoComida,
            fecha = fecha
        )
    }

    fun cerrar() {
        datosActuales = null
    }
}

@Composable
fun PantallaRegistroRepetido() {

    val datos = MensajeRegistroRepetido.datosActuales ?: return

    Dialog(
        onDismissRequest = {
            MensajeRegistroRepetido.cerrar()
        },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Color.Black.copy(alpha = 0.60f)
                )
                .padding(
                    horizontal = 18.dp,
                    vertical = 20.dp
                ),
            contentAlignment = Alignment.Center
        ) {

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 355.dp)
                    .heightIn(max = maxHeight * 0.88f)
                    .shadow(
                        elevation = 18.dp,
                        shape = RoundedCornerShape(22.dp)
                    )
                    .border(
                        width = 3.dp,
                        color = Color(0xFF42B75B),
                        shape = RoundedCornerShape(22.dp)
                    ),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 0.dp
                )
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(
                            rememberScrollState()
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    // Encabezado
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = Color(0xFF063B78)
                            )
                            .padding(
                                horizontal = 18.dp,
                                vertical = 14.dp
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    color = Color.White,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {

                            Icon(
                                imageVector = Icons.Rounded.Check,
                                contentDescription = null,
                                tint = Color(0xFF27945A),
                                modifier = Modifier.size(31.dp)
                            )
                        }

                        Spacer(
                            modifier = Modifier.width(13.dp)
                        )

                        Text(
                            text = "YA ESTÁS REGISTRADO",
                            color = Color.White,
                            fontSize = 17.sp,
                            lineHeight = 21.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 20.dp,
                                vertical = 17.dp
                            ),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        // Nombre
                        Text(
                            text = datos.nombre,
                            color = Color(0xFF171717),
                            fontSize = 20.sp,
                            lineHeight = 25.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(
                            modifier = Modifier.height(13.dp)
                        )

                        // Foto
                        Box(
                            modifier = Modifier
                                .size(108.dp)
                                .background(
                                    color = Color(0xFFF1F5F9),
                                    shape = CircleShape
                                )
                                .border(
                                    width = 3.dp,
                                    color = Color(0xFFE2E8F0),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {

                            if (datos.fotoUrl.isNotBlank()) {

                                AsyncImage(
                                    model = datos.fotoUrl,
                                    contentDescription = "Foto del estudiante",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(100.dp)
                                        .clip(CircleShape)
                                )

                            } else {

                                Icon(
                                    imageVector = Icons.Rounded.Person,
                                    contentDescription = "Sin fotografía",
                                    tint = Color(0xFF64748B),
                                    modifier = Modifier.size(52.dp)
                                )
                            }
                        }

                        Spacer(
                            modifier = Modifier.height(12.dp)
                        )

                        // Carrera
                        Text(
                            text = datos.carrera,
                            color = Color(0xFF475569),
                            fontSize = 14.sp,
                            lineHeight = 19.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(
                            modifier = Modifier.height(15.dp)
                        )

                        // Mensaje
                        Text(
                            text = "Este QR ya fue utilizado previamente para este tipo de comida.",
                            color = Color(0xFF64748B),
                            fontSize = 13.sp,
                            lineHeight = 19.sp,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(
                            modifier = Modifier.height(16.dp)
                        )

                        // Tipo de comida y fecha
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(
                                space = 10.dp,
                                alignment = Alignment.CenterHorizontally
                            ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            TarjetaDatoRepetido(
                                valor = datos.tipoComida,
                                icono = Icons.Rounded.Restaurant
                            )

                            TarjetaDatoRepetido(
                                valor = datos.fecha,
                                icono = Icons.Rounded.CalendarMonth
                            )
                        }

                        Spacer(
                            modifier = Modifier.height(17.dp)
                        )

                        // Botón
                        Button(
                            onClick = {
                                MensajeRegistroRepetido.cerrar()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF063B78),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {

                            Icon(
                                imageVector = Icons.Rounded.Check,
                                contentDescription = null,
                                modifier = Modifier.size(21.dp)
                            )

                            Spacer(
                                modifier = Modifier.width(8.dp)
                            )

                            Text(
                                text = "Entendido",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TarjetaDatoRepetido(
    valor: String,
    icono: ImageVector
) {

    Card(
        modifier = Modifier
            .height(46.dp)
            .widthIn(
                min = 90.dp,
                max = 140.dp
            )
            .shadow(
                elevation = 3.dp,
                shape = RoundedCornerShape(11.dp)
            ),
        shape = RoundedCornerShape(11.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {

        Row(
            modifier = Modifier
                .height(46.dp)
                .padding(horizontal = 11.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {

            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = Color(0xFF27945A),
                modifier = Modifier.size(20.dp)
            )

            Spacer(
                modifier = Modifier.width(7.dp)
            )

            Text(
                text = valor,
                color = Color(0xFF171717),
                fontSize = 13.sp,
                lineHeight = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}