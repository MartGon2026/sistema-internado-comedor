package com.example.jetcompos.mensajeria

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.delay

@Composable
fun PantallaMensajeGlobal() {

    val mensaje = Mensajeria.mensajeActual ?: return

    // Éxito, error y advertencia desaparecen después de 2.5 segundos.
    if (mensaje !is ResultadoMensaje.Confirmacion) {
        LaunchedEffect(mensaje) {
            delay(2500)
            Mensajeria.limpiar()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(100f)
            .background(
                Color(0xFF0F172A).copy(alpha = 0.65f)
            ),
        contentAlignment = Alignment.Center
    ) {

        when (mensaje) {

            is ResultadoMensaje.Exito -> {

                MensajeEstado(
                    texto = mensaje.mensaje,
                    icono = Icons.Rounded.Check,
                    colorPrincipal = Color(0xFF16A34A),
                    fondoIcono = Color(0xFFDCFCE7),
                    fondoInferior = Color(0xFFE7F9EC)
                )
            }

            is ResultadoMensaje.Error -> {

                MensajeEstado(
                    texto = mensaje.mensaje,
                    icono = Icons.Rounded.Close,
                    colorPrincipal = Color(0xFFDC2626),
                    fondoIcono = Color(0xFFFEE2E2),
                    fondoInferior = Color(0xFFFFEAEA)
                )
            }

            is ResultadoMensaje.Advertencia -> {

                MensajeEstado(
                    texto = mensaje.mensaje,
                    icono = Icons.Rounded.WarningAmber,
                    colorPrincipal = Color(0xFFF59E0B),
                    fondoIcono = Color(0xFFFFF3D6),
                    fondoInferior = Color(0xFFFFF4DE)
                )
            }

            // El diseño de confirmación se mantiene igual.
            is ResultadoMensaje.Confirmacion -> {

                Card(
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(0.dp),
                    modifier = Modifier
                        .padding(24.dp)
                        .shadow(
                            elevation = 20.dp,
                            spotColor = Color.Black.copy(alpha = 0.10f),
                            ambientColor = Color.Black.copy(alpha = 0.05f),
                            shape = RoundedCornerShape(28.dp)
                        )
                ) {

                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        val colorConfirmacion = Color(0xFF3B82F6)
                        val fondoConfirmacion = Color(0xFFEFF6FF)

                        Box(
                            modifier = Modifier.size(90.dp),
                            contentAlignment = Alignment.TopCenter
                        ) {

                            // Sombra del círculo.
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .align(Alignment.TopCenter)
                                    .offset(y = 4.dp)
                                    .background(
                                        color = Color(0xFFCBD5E1),
                                        shape = CircleShape
                                    )
                            )

                            // Círculo principal.
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .align(Alignment.TopCenter)
                                    .zIndex(1f)
                                    .background(
                                        color = fondoConfirmacion,
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {

                                Text(
                                    text = "?",
                                    fontSize = 42.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colorConfirmacion
                                )
                            }
                        }

                        Spacer(
                            modifier = Modifier.height(24.dp)
                        )

                        Text(
                            text = mensaje.mensaje,
                            fontSize = 18.sp,
                            lineHeight = 25.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1E293B),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(
                            modifier = Modifier.height(32.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {

                            Button(
                                onClick = {
                                    Mensajeria.limpiar()
                                    mensaje.onCancel.invoke()
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFF1F5F9)
                                ),
                                shape = CircleShape,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                            ) {

                                Text(
                                    text = "Cancelar",
                                    color = Color(0xFF64748B),
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            Button(
                                onClick = {
                                    Mensajeria.limpiar()
                                    mensaje.onConfirm.invoke()
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF0F172A)
                                ),
                                shape = CircleShape,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                            ) {

                                Text(
                                    text = "Aceptar",
                                    color = Color.White,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MensajeEstado(
    texto: String,
    icono: ImageVector,
    colorPrincipal: Color,
    fondoIcono: Color,
    fondoInferior: Color
) {

    Card(
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(0.dp),
        modifier = Modifier
            .padding(horizontal = 28.dp)
            .fillMaxWidth()
            .widthIn(max = 350.dp)
            .heightIn(min = 330.dp)
            .shadow(
                elevation = 22.dp,
                spotColor = Color.Black.copy(alpha = 0.20f),
                ambientColor = Color.Black.copy(alpha = 0.08f),
                shape = RoundedCornerShape(26.dp)
            )
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.80f),
                shape = RoundedCornerShape(26.dp)
            )
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 330.dp)
        ) {

            // Fondo decorativo curvo.
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(105.dp)
                    .align(Alignment.BottomCenter)
            ) {

                val fondoCurvo = Path().apply {

                    moveTo(
                        x = 0f,
                        y = size.height * 0.35f
                    )

                    cubicTo(
                        x1 = size.width * 0.25f,
                        y1 = size.height * 0.85f,
                        x2 = size.width * 0.68f,
                        y2 = size.height * 0.90f,
                        x3 = size.width,
                        y3 = size.height * 0.35f
                    )

                    lineTo(
                        x = size.width,
                        y = size.height
                    )

                    lineTo(
                        x = 0f,
                        y = size.height
                    )

                    close()
                }

                drawPath(
                    path = fondoCurvo,
                    color = fondoInferior
                )

                drawLine(
                    color = colorPrincipal,
                    start = Offset(
                        x = 0f,
                        y = size.height - 4.dp.toPx()
                    ),
                    end = Offset(
                        x = size.width,
                        y = size.height - 4.dp.toPx()
                    ),
                    strokeWidth = 6.dp.toPx()
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 25.dp,
                        end = 25.dp,
                        top = 32.dp,
                        bottom = 72.dp
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Resplandor exterior.
                Box(
                    modifier = Modifier
                        .size(115.dp)
                        .background(
                            color = fondoIcono.copy(alpha = 0.50f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    // Círculo principal.
                    Box(
                        modifier = Modifier
                            .size(94.dp)
                            .background(
                                color = fondoIcono,
                                shape = CircleShape
                            )
                            .border(
                                width = 3.dp,
                                color = colorPrincipal,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {

                        Icon(
                            imageVector = icono,
                            contentDescription = null,
                            tint = colorPrincipal,
                            modifier = Modifier.size(55.dp)
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(27.dp)
                )

                Text(
                    text = texto,
                    color = Color(0xFF1E293B),
                    fontSize = 20.sp,
                    lineHeight = 27.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}