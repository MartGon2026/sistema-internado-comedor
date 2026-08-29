package com.example.qr

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.qr.conexion_DB.Models.historial_Modal
import com.example.qr.conexion_DB.Repositorio.estudiante_Reposi
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.launch

class HistorialAdapter(
    private val listaHistorial: List<historial_Modal.historial_Mo>,
    private val lifecycleOwner: LifecycleOwner
) : RecyclerView.Adapter<HistorialAdapter.ViewHolder>() {

    class ViewHolder(vista: View) : RecyclerView.ViewHolder(vista) {
        val imagen: CircleImageView = vista.findViewById(R.id.imgEstudante)
        val txtNombre: TextView = vista.findViewById(R.id.txtNombre)
        val txtId: TextView = vista.findViewById(R.id.txtId)
        val txtCodigo: TextView = vista.findViewById(R.id.txtCodigo)
        val txtEstado: TextView = vista.findViewById(R.id.txtEstado)
        val dotEstado: View = vista.findViewById(R.id.dotEstado)
        val franja: View = vista.findViewById(R.id.viewEstadoColor)
        val badge: View = vista.findViewById(R.id.containerBadge)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.inten_historial, parent, false)
        return ViewHolder(vista)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val registro = listaHistorial[position]
        
        holder.txtId.text = "ID: ${registro.carnet}"
        holder.txtNombre.text = "Cargando..."
        
        val esFuera = registro.Estado == "FUERA"
        
        if (esFuera) {
            holder.txtEstado.text = "FUERA"
            holder.txtEstado.setTextColor(0xFFDC2626.toInt())
            holder.dotEstado.setBackgroundResource(R.drawable.dot_rejo)
            holder.franja.setBackgroundColor(0xFFDC2626.toInt())
            holder.badge.setBackgroundResource(R.drawable.bg_badge_red)
            holder.txtCodigo.text = "Salida: ${registro.hora_salida}"
        } else {
            holder.txtEstado.text = "DENTRO"
            holder.txtEstado.setTextColor(0xFF16A34A.toInt())
            holder.dotEstado.setBackgroundResource(R.drawable.dot_green)
            holder.franja.setBackgroundColor(0xFF16A34A.toInt())
            holder.badge.setBackgroundResource(R.drawable.bg_badge_green)
            holder.txtCodigo.text = "Entrada: ${registro.hora_entrada ?: "--"}"
        }

        // Cargar datos del estudiante dinámicamente
        lifecycleOwner.lifecycleScope.launch {
            val estudiante = estudiante_Reposi.datoEstudiante(registro.carnet)
            if (estudiante != null) {
                holder.txtNombre.text = "${estudiante.nombres} ${estudiante.apellidos}"
                
                Glide.with(holder.itemView.context)
                    .load(estudiante.foto_estudi)
                    .placeholder(R.drawable.baseline_person_24)
                    .into(holder.imagen)
            }
        }
    }

    override fun getItemCount() = listaHistorial.size
}