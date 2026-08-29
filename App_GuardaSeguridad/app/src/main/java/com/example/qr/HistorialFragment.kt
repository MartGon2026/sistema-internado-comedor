package com.example.qr

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.qr.conexion_DB.Models.historial_Modal
import com.example.qr.conexion_DB.Repositorio.historial_Reposi
import com.example.qr.fecha_Hora_y_TipoComida.FechaHoraUtil
import com.google.android.material.chip.ChipGroup
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HistorialFragment : Fragment(R.layout.activity_historial) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: HistorialAdapter
    private lateinit var chipGroup: ChipGroup
    private lateinit var loadingHistorial: View
    
    private var listaCompleta: List<historial_Modal.historial_Mo> = emptyList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerPersonas)
        chipGroup = view.findViewById(R.id.chipGroupFiltros)
        loadingHistorial = view.findViewById(R.id.containerLoadingHistorial)
        
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)

        configurarFiltros()
        cargarHistorial()
    }

    private fun configurarFiltros() {
        chipGroup.setOnCheckedStateChangeListener { _, checkedIds ->
            val filtro = when (checkedIds.firstOrNull()) {
                R.id.chipFuera -> "FUERA"
                R.id.chipDentro -> "DENTRO"
                else -> "TODOS"
            }
            aplicarFiltro(filtro)
        }
    }

    private fun aplicarFiltro(tipo: String) {
        val listaFiltrada = when (tipo) {
            "FUERA" -> listaCompleta.filter { it.Estado == "FUERA" }
            "DENTRO" -> listaCompleta.filter { it.Estado == "DENTRO" }
            else -> listaCompleta
        }
        
        adapter = HistorialAdapter(listaFiltrada, viewLifecycleOwner)
        recyclerView.adapter = adapter
    }

    private fun cargarHistorial() {
        loadingHistorial.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                val fechaHoy = FechaHoraUtil.obtenerFechaActual()
                listaCompleta = withContext(Dispatchers.IO) {
                    historial_Reposi.obtenerHistorialCompleto(fechaHoy)
                }

                // Al cargar por primera vez, respetamos el chip seleccionado (por defecto Todos)
                val filtroActual = when (chipGroup.checkedChipId) {
                    R.id.chipFuera -> "FUERA"
                    R.id.chipDentro -> "DENTRO"
                    else -> "TODOS"
                }
                aplicarFiltro(filtroActual)

                if (listaCompleta.isEmpty()) {
                    Toast.makeText(requireContext(), "No hay movimientos registrados", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                loadingHistorial.visibility = View.GONE
            }
        }
    }
}