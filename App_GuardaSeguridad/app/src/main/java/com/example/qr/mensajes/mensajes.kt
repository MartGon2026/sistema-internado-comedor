package com.example.qr.mensajes

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.example.qr.R
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.delay

object mensajes{
    enum class TipoMensaje { ERROR, EXITO,  ADVERTENCIA  }

    suspend fun mostrar(
        context: Context,
        layoutInflater: LayoutInflater,
        titulo: String,
        mensaje: String,
        tipo: TipoMensaje = TipoMensaje.ERROR,
        tiempo: Long = 2500
    ) {
        val dialogView = layoutInflater.inflate(R.layout.mensaje, null)

        val dialog = AlertDialog.Builder(context).setView(dialogView).create()

        val cardDialog = dialogView.findViewById<MaterialCardView>(R.id.cardDialogMensaje)
        val txtIcono = dialogView.findViewById<TextView>(R.id.txtIconoDialogo)
        val txtTitulo = dialogView.findViewById<TextView>(R.id.txtTituloDialogo)
        val txtMensaje = dialogView.findViewById<TextView>(R.id.txtMensajeDialogo)

        val colorPrincipal: Int
        val colorFondoIcono: Int
        val icono: String

        when (tipo) {
            TipoMensaje.ERROR -> {
                colorPrincipal = Color.parseColor("#EF4444")
                colorFondoIcono = Color.parseColor("#FEE2E2")
                icono = "❌"
            }

            TipoMensaje.EXITO -> {
                colorPrincipal = Color.parseColor("#10B981")
                colorFondoIcono = Color.parseColor("#D1FAE5")
                icono = "✅"
            }

            TipoMensaje.ADVERTENCIA -> {
                colorPrincipal = Color.parseColor("#F59E0B")
                colorFondoIcono = Color.parseColor("#FEF3C7")
                icono = "⚠️"
            }
        }

        cardDialog.strokeColor = colorPrincipal

        val fondoIcono = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(colorFondoIcono)
            setStroke(2, Color.WHITE)
        }

        txtIcono.background = fondoIcono
        txtIcono.text = icono
        txtTitulo.text = titulo
        txtMensaje.text = mensaje

        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)

        dialog.show()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setDimAmount(0.55f)

        delay(tiempo)

        if (dialog.isShowing) {
            dialog.dismiss()
        }
    }
}