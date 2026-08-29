package com.example.qr

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlin.math.roundToInt

import com.example.qr.GraficoPastel.GraficaCircularView
import com.example.qr.conexion_DB.Models.historial_Modal
import com.example.qr.conexion_DB.Repositorio.estudiante_Reposi
import com.example.qr.conexion_DB.Repositorio.historial_Reposi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DashboardFragment : Fragment(R.layout.dasword_activity) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val loading = view.findViewById<View>(R.id.containerLoadingDashboard)
        loading.visibility = View.VISIBLE
        
        lifecycleScope.launch {
            try {
                val TotalDatosEstu = estudiante_Reposi.obtenerUnTotalEstudiantes()
                val totalSalidasEntradas = historial_Reposi.obtenerUnTotalSalidaEntrada()
                val ultimosMovimientos = historial_Reposi.obtenerUltimos3Movimientos()

                val porcentajeDentro = if (TotalDatosEstu.total > 0) {
                    (TotalDatosEstu.dentro.toDouble() / TotalDatosEstu.total.toDouble() * 100).roundToInt()
                } else { 0 }

                actualizarDashboard(
                    view = view, 
                    TotalDatosEstu.total, 
                    TotalDatosEstu.fuera, 
                    TotalDatosEstu.dentro,
                    porcentajeDentro.toString(),
                    ultimosMovimientos
                )
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                loading.visibility = View.GONE
            }
        }
    }

    private fun Int.dp(view: View): Int {
        return (this * view.resources.displayMetrics.density).toInt()
    }

    private fun actualizarDashboard(
        view: View, 
        totalEstudiantes: Int, 
        salieron: Int, 
        entraron: Int, 
        porcentaje: String,
        ultimosMovimientos: List<historial_Modal.historial_Mo>
    ) {
        // Tarjetas superiores
        view.findViewById<TextView>(R.id.textoTotalEstudiantes).text = totalEstudiantes.toString()
        view.findViewById<TextView>(R.id.textoTotalSalieron).text = salieron.toString()
        view.findViewById<TextView>(R.id.textoTotalEntraron).text = entraron.toString()
        view.findViewById<TextView>(R.id.textoPorcentajeDentroTarjeta).text = "$porcentaje%"

        // Gráfica circular
        val grafica = view.findViewById<GraficaCircularView>(R.id.graficoControlRegreso)
        grafica.establecerDatos(entraron = entraron, faltan = salieron)

        // Centro de la gráfica: Porcentaje de Ocupación
        view.findViewById<TextView>(R.id.textoPorcentajeCentro).text = "$porcentaje%"

        // Leyenda de la gráfica
        view.findViewById<TextView>(R.id.textoEntraronGrafico).text = entraron.toString()
        view.findViewById<TextView>(R.id.textoFaltanGrafico).text = salieron.toString()
        
        val porcentajeFuera = if (totalEstudiantes > 0) {
            (salieron.toDouble() / totalEstudiantes.toDouble() * 100).roundToInt()
        } else { 0 }
        
        view.findViewById<TextView>(R.id.textoPorcentajeEntraron).text = "$porcentaje%"
        view.findViewById<TextView>(R.id.textoPorcentajeFaltan).text = "$porcentajeFuera%"

        // Sección: Últimos Movimientos
        val contenedor = view.findViewById<android.widget.LinearLayout>(R.id.contenedorUltimosMovimientos)
        val textoSinDato = view.findViewById<TextView>(R.id.textoSinMovimientos)

        if (ultimosMovimientos.isNotEmpty()) {
            textoSinDato.visibility = View.GONE
            contenedor.removeAllViews()
            
            ultimosMovimientos.forEach { mov ->
                val itemView = layoutInflater.inflate(R.layout.item_movimiento_reciente, contenedor, false)
                val imgIcono = itemView.findViewById<ImageView>(R.id.imgIconoMovimiento)
                val containerIcono = itemView.findViewById<View>(R.id.containerIcono)
                val txtNombre = itemView.findViewById<TextView>(R.id.txtNombreMovimiento)
                val txtCarnet = itemView.findViewById<TextView>(R.id.txtCarnetMovimiento)
                val txtEstado = itemView.findViewById<TextView>(R.id.txtEstadoMovimiento)
                val txtHora = itemView.findViewById<TextView>(R.id.txtHoraMovimiento)
                
                val esEntrada = mov.Estado == "DENTRO"
                
                // Configurar Estilo según tipo de movimiento
                if (esEntrada) {
                    imgIcono.setImageResource(android.R.drawable.ic_menu_revert)
                    imgIcono.setTag("ENTRADA")
                    imgIcono.imageTintList = android.content.res.ColorStateList.valueOf(0xFF16A34A.toInt())
                    containerIcono.setBackgroundResource(R.drawable.bg_circle_green_soft)
                    txtEstado.text = "ENTRADA"
                    txtEstado.setTextColor(0xFF16A34A.toInt())
                } else {
                    imgIcono.setImageResource(android.R.drawable.ic_menu_send)
                    imgIcono.setTag("SALIDA")
                    imgIcono.imageTintList = android.content.res.ColorStateList.valueOf(0xFFDC2626.toInt())
                    containerIcono.setBackgroundResource(R.drawable.bg_circle_red_soft)
                    txtEstado.text = "SALIDA"
                    txtEstado.setTextColor(0xFFDC2626.toInt())
                }

                txtHora.text = if (esEntrada) mov.hora_entrada else mov.hora_salida
                txtCarnet.text = mov.carnet

                lifecycleScope.launch {
                    val estudiante = estudiante_Reposi.datoEstudiante(mov.carnet)
                    txtNombre.text = "${estudiante?.nombres ?: "Usuario"} ${estudiante?.apellidos ?: ""}"
                }

                contenedor.addView(itemView)
                
                // Añadir una línea divisoria sutil (opcional, pero ayuda al orden)
                val line = View(view.context)
                val params = android.widget.LinearLayout.LayoutParams(
                    android.widget.LinearLayout.LayoutParams.MATCH_PARENT, 1
                )
                params.setMargins(56.dp(view), 0, 0, 0) // Empieza después del icono
                line.layoutParams = params
                line.setBackgroundColor(0xFFF3F4F6.toInt())
                contenedor.addView(line)
            }
        }
    }
}