package com.example.qr

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.qr.guardarDatosTelefono.datosEnMemoria
import com.google.android.material.button.MaterialButton
import de.hdodenhof.circleimageview.CircleImageView

class PerfilFragment : Fragment(R.layout.fragment_perfil) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imgPerfil = view.findViewById<CircleImageView>(R.id.imgPerfilGrande)
        val txtNombre = view.findViewById<TextView>(R.id.txtNombrePerfil)
        val txtUsuario = view.findViewById<TextView>(R.id.txtUsuarioPerfil)
        val txtTurno = view.findViewById<TextView>(R.id.txtTurnoPerfil)
        val txtSexo = view.findViewById<TextView>(R.id.txtSexoPerfil)
        val btnLogout = view.findViewById<MaterialButton>(R.id.btnLogout)

        val datos = datosEnMemoria.obtener(requireContext())

        if (datos != null) {
            txtNombre.text = "${datos.nombre} ${datos.apellido}"
            txtUsuario.text = datos.usuario ?: "No disponible"
            txtTurno.text = datos.turno ?: "No asignado"
            txtSexo.text = datos.sexo ?: "No especificado"

            Glide.with(this)
                .load(datos.foto)
                .placeholder(R.drawable.baseline_person_24)
                .error(R.drawable.baseline_person_24)
                .into(imgPerfil)
        }

        btnLogout.setOnClickListener {
            cerrarSesion()
        }
    }

    private fun cerrarSesion() {
        // Borramos los datos de memoria
      //  datosEnMemoria.eliminar(requireContext())

        // Redirigimos al Login y limpiamos el historial de actividades
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }
}
