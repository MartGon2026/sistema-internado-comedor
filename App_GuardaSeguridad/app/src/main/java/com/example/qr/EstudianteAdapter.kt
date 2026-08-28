package com.example.qr

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.qr.conexion_DB.Models.estudiante_Modal.estudiante_Mo
import de.hdodenhof.circleimageview.CircleImageView

class EstudianteAdapter(
    private val listaEstudiantes: List<estudiante_Mo>) :
    RecyclerView.Adapter<EstudianteAdapter.VistaContenedor>() {

    inner class VistaContenedor(vista: View) : RecyclerView.ViewHolder(vista) {
        val imagenPerfil: CircleImageView = vista.findViewById(R.id.imgEstudante)
        val textoNombre: TextView = vista.findViewById(R.id.txtNombre)
        val textoId: TextView = vista.findViewById(R.id.txtId)
        val textoCodigo: TextView = vista.findViewById(R.id.txtCodigo)
        val textoEstado: TextView = vista.findViewById(R.id.txtEstado)
        val puntoEstado: View = vista.findViewById(R.id.dotEstado)
         val franjaEstado: View = vista.findViewById(R.id.viewEstadoColor)
        val contenedorBadge: View = vista.findViewById(R.id.containerBadge)
    }

    override fun onCreateViewHolder(padre: ViewGroup, tipoDeVista: Int): VistaContenedor {
        val vista = LayoutInflater.from(padre.context).inflate(R.layout.inten_historial, padre, false)
        return VistaContenedor(vista)
    }

    override fun onBindViewHolder(contenedor: VistaContenedor, posicion: Int) {
        val estudiante = listaEstudiantes[posicion]

        contenedor.textoNombre.text = "${estudiante.nombres} ${estudiante.apellidos}"
        contenedor.textoId.text = "ID: ${estudiante.carnet}"
        contenedor.textoCodigo.text = estudiante.carrera
        
        val esFuera = estudiante.estado_internado == "FUERA"
        contenedor.textoEstado.text = if (esFuera) "FUERA" else "DENTRO"
        
        if (esFuera) {
            contenedor.textoEstado.setTextColor(0xFFDC2626.toInt()) // Rojo
            contenedor.puntoEstado.setBackgroundResource(R.drawable.dot_rejo)
            contenedor.franjaEstado.setBackgroundColor(0xFFDC2626.toInt())
            contenedor.contenedorBadge.setBackgroundResource(R.drawable.bg_badge_red)
        } else {
            contenedor.textoEstado.setTextColor(0xFF16A34A.toInt()) // Verde
            contenedor.puntoEstado.setBackgroundResource(R.drawable.dot_green)
            contenedor.franjaEstado.setBackgroundColor(0xFF16A34A.toInt())
            contenedor.contenedorBadge.setBackgroundResource(R.drawable.bg_badge_green)
        }

        // CARGA DE FOTO
        Glide.with(contenedor.itemView.context).load(estudiante.foto_estudi)
            .placeholder(R.drawable.baseline_person_24)
            .error(R.drawable.baseline_person_24).skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE).into(contenedor.imagenPerfil)
    }

    override fun getItemCount(): Int = listaEstudiantes.size
}