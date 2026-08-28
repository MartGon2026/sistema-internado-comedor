package com.example.jetcompos

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.SetMeal
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.example.jetcompos.conexion_DB.Repositorio.asistencia_Reposi


data class ModeloHistorial(
    val titulo: String,
    val fecha: String,
    val hora: String,
    val icono: ImageVector,
    val estado: String
)

object RepositorioHistorial {

    suspend fun obtenerListaHistorial(
        carnet: String,
        fechaActual: String
    ): List<ModeloHistorial> {

        val lista = mutableListOf<ModeloHistorial>()
        val tiempoMilisegundos = System.currentTimeMillis()

        // 2. Formateamos la fecha usando el formateador estándar de Android (compatible con todo SDK)
        val formatoKotlin = android.text.format.DateFormat.format("d MMM yyyy", tiempoMilisegundos)

        // 3. Lo convertimos a un String limpio para nuestra interfaz
        val fechaHoyFormateada = formatoKotlin.toString()
       // val fechaActual = FechaHoraUtil.obtenerFechaActual()

        // DESAYUNO
        val desayuno =  asistencia_Reposi.obtenerDatosAsistenciaCarnet( carnet,"DESAYUNO",   fechaActual )

        if (desayuno != null) {
            lista.add(
                ModeloHistorial(
                    titulo = desayuno.tipo_comida,
                    fecha = fechaHoyFormateada,
                    hora = desayuno.hora,
                    icono = Icons.Default.Coffee,
                    estado = "Asistió"
                )
            )
        }

        // ALMUERZO
        val almuerzo =
            asistencia_Reposi.obtenerDatosAsistenciaCarnet(
                carnet,
                "ALMUERZO",
                fechaActual
            )

        if (almuerzo != null) {
            lista.add(
                ModeloHistorial(
                    titulo = almuerzo.tipo_comida,
                    fecha = fechaHoyFormateada,
                    hora = almuerzo.hora,
                    icono = Icons.Default.Restaurant,
                    estado = "Asistió"
                )
            )
        }

        // CENA
        val cena =
            asistencia_Reposi.obtenerDatosAsistenciaCarnet(
                carnet,
                "CENA",
                fechaActual
            )

        if (cena != null) {
            lista.add(
                ModeloHistorial(
                    titulo = cena.tipo_comida,
                    fecha = fechaHoyFormateada,
                    hora = cena.hora,
                    icono = Icons.Default.SetMeal,
                    estado = "Asistió"
                )
            )
        }

        return lista
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHistorial() {
    Column {
        Text("Preview no disponible porque los datos vienen de Supabase")
    }
}