package com.example.qr.GraficoPastel

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class GraficaCircularView @JvmOverloads constructor(
    context: Context,
    atributos: AttributeSet? = null
) : View(context, atributos) {

    private val pintura = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.BUTT
    }

    private val areaGrafica = RectF()

    private var cantidadEntraron = 95f
    private var cantidadFaltan = 33f

    private val colorEntraron = Color.parseColor("#00B53F")
    private val colorFaltan = Color.parseColor("#FFA000")

    private val grosorGrafica = 25f * resources.displayMetrics.density

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val total = cantidadEntraron + cantidadFaltan

        if (total <= 0f) return

        val espacio = grosorGrafica / 2f

        areaGrafica.set(
            espacio,
            espacio,
            width.toFloat() - espacio,
            height.toFloat() - espacio
        )

        pintura.strokeWidth = grosorGrafica

        val anguloEntraron = (cantidadEntraron / total) * 360f
        val anguloFaltan = (cantidadFaltan / total) * 360f

        // Parte verde: estudiantes que entraron
        pintura.color = colorEntraron

        canvas.drawArc(
            areaGrafica,
            -90f,
            anguloEntraron,
            false,
            pintura
        )

        // Parte naranja: estudiantes que aún faltan
        pintura.color = colorFaltan

        canvas.drawArc(
            areaGrafica,
            -90f + anguloEntraron,
            anguloFaltan,
            false,
            pintura
        )
    }

    fun establecerDatos(entraron: Int, faltan: Int) {
        cantidadEntraron = entraron.toFloat()
        cantidadFaltan = faltan.toFloat()

        invalidate()
    }
}