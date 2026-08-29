package com.example.jetcompos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ItemHistorial(registro: ModeloHistorial) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
        colors = CardDefaults.cardColors(containerColor = coloress.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Parte Izquierda (Icono + Texto)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                Box(
                    modifier = Modifier.size(40.dp).background(coloress.FondoGris, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = registro.icono,
                        contentDescription = null,
                        tint = coloress.PrimaryGreen,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Column {
                    Text( text = registro.titulo, fontWeight = FontWeight.SemiBold,fontSize = 15.sp, color = coloress.TextoPrincipal
                    )
                    Text( text = registro.fecha, fontSize = 12.sp, color = coloress.TextoSecundario )
                }
            }

            // Parte Derecha (Estado + Hora)
            Column(horizontalAlignment = Alignment.End) {
                Text( text = registro.estado, color = coloress.PrimaryGreen, fontWeight = FontWeight.Bold, fontSize = 13.sp )
                Text( text = registro.hora, fontSize = 11.sp, color = coloress.GrisClaro )
            }
        }
    }
}