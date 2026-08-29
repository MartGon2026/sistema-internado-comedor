package com.example.qr.mensajes

import android.app.Dialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide
import com.example.qr.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

class MensajeMovimiento : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStyle(
            STYLE_NORMAL,
            R.style.TemaMensajePantallaCompleta
        )
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val vista = requireActivity()
            .layoutInflater
            .inflate(R.layout.resultado, null)

        val dialogo = android.app.AlertDialog.Builder(requireContext())
            .setView(vista)
            .create()

        val argumentos = requireArguments()

        configurarVista(
            vista = vista,
            nombre = argumentos.getString(ARG_NOMBRE).orEmpty(),
            carnet = argumentos.getString(ARG_CARNET).orEmpty(),
            carrera = argumentos.getString(ARG_CARRERA).orEmpty(),
            movimiento = argumentos.getString(ARG_MOVIMIENTO).orEmpty(),
            fotoUrl = argumentos.getString(ARG_FOTO_URL)
        )

        dialogo.setCanceledOnTouchOutside(false)
        dialogo.setCancelable(true)

        return dialogo
    }

    override fun onStart() {
        super.onStart()

        dialog?.window?.apply {
            // Fondo transparente para ver el dim (oscurecimiento) detrás
            setBackgroundDrawableResource(android.R.color.transparent)

            // Ajustamos el ancho al 90% de la pantalla y la altura al contenido
            val width = (resources.displayMetrics.widthPixels * 0.90).toInt()
            setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT)

            // Forzamos el centrado absoluto
            setGravity(Gravity.CENTER)
        }
    }

    private fun configurarVista( vista: View,nombre: String,    carnet: String,   carrera: String,
        movimiento: String,
        fotoUrl: String?
    ) {

        val fondoMensaje = vista.findViewById<MaterialCardView>( R.id.MensajeResultado )

        val fotoPerfil =
            vista.findViewById<ImageView>(
                R.id.imgFotoEstudiante
            )

        val textoNombre =
            vista.findViewById<TextView>(
                R.id.txtnombreEstudi
            )

        val textoCarnet =
            vista.findViewById<TextView>(
                R.id.txtCarnetEstudiante
            )

        val textoCarrera =
            vista.findViewById<TextView>(
                R.id.txtinforResultado
            )

        val tarjetaEncabezado =
            vista.findViewById<MaterialCardView>(
                R.id.tarjetaEncabezadoValidacion
            )

        val iconoValidacion =
            vista.findViewById<ImageView>(
                R.id.iconoValidacion
            )

        val textoRegistro =
            vista.findViewById<TextView>(
                R.id.inforComida
            )



        val botonAceptar =
            vista.findViewById<MaterialButton>(
                R.id.btguardar
            )

        // Datos del estudiante
        textoNombre.text = nombre
        textoCarnet.text = "Carnet: $carnet"
        textoCarrera.text = carrera

        // Fotografía
        Glide.with(this)
            .load(fotoUrl)
            .placeholder(R.drawable.baseline_person_24)
            .error(R.drawable.baseline_person_24)
            .centerCrop()
            .into(fotoPerfil)

        val esEntrada = movimiento.equals(
            "ENTRADA",
            ignoreCase = true
        )

        // Mantener el fondo blanco de la tarjeta (coherencia con el diseño nuevo)
        fondoMensaje.setCardBackgroundColor(Color.WHITE)

        if (esEntrada) {
            // ENTRADA: Colores verdes modernos
            tarjetaEncabezado.setCardBackgroundColor(Color.parseColor("#F0FDF4")) // Verde muy suave
            iconoValidacion.imageTintList = ColorStateList.valueOf(Color.parseColor("#16A34A"))
            
            textoRegistro.text = "¡Entrada registrada!"
            textoRegistro.setTextColor(Color.parseColor("#16A34A"))

            botonAceptar.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#16A34A"))
        } else {
            // SALIDA: Colores rojos modernos
            tarjetaEncabezado.setCardBackgroundColor(Color.parseColor("#FFF1F1")) // Rojo muy suave
            iconoValidacion.imageTintList = ColorStateList.valueOf(Color.parseColor("#DC2626"))
            iconoValidacion.setImageResource(R.drawable.outline_history_24) // Icono de historial/salida
            
            textoRegistro.text = "¡Salida registrada!"
            textoRegistro.setTextColor(Color.parseColor("#DC2626"))

            botonAceptar.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#DC2626"))
            // Usamos un icono diferente para salida si existe, o mantenemos el check
        }

        botonAceptar.setOnClickListener {
            dismiss()
        }
    }

    companion object {

        private const val ARG_NOMBRE = "nombre"
        private const val ARG_CARNET = "carnet"
        private const val ARG_CARRERA = "carrera"
        private const val ARG_MOVIMIENTO = "movimiento"
        private const val ARG_FOTO_URL = "fotoUrl"

        fun mostrar(
            fragmentManager: FragmentManager,
            nombre: String,
            carnet: String,
            carrera: String,
            movimiento: String,
            fotoUrl: String?
        ) {

            val mensaje = MensajeMovimiento().apply {

                arguments = Bundle().apply {
                    putString(ARG_NOMBRE, nombre)
                    putString(ARG_CARNET, carnet)
                    putString(ARG_CARRERA, carrera)
                    putString(ARG_MOVIMIENTO, movimiento)
                    putString(ARG_FOTO_URL, fotoUrl)
                }
            }

            mensaje.show(
                fragmentManager,
                "MensajeMovimiento"
            )
        }
    }
}