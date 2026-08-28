package com.example.qr

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnNavegacion = findViewById<BottomNavigationView>(R.id.bottom_nav)
        
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer, ScannerCode()).commit()
        }

        btnNavegacion.setOnItemSelectedListener {item ->
            when (item.itemId){
                R.id.nav_scanner -> {
                    supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer, ScannerCode()).commit()
                    true
                }

                R.id.nav_historial -> {
                    supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer, HistorialFragment()).commit()
                    true
                }

              R.id.nav_dasword -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, DashboardFragment())
                        .commit()
                    true
                }

                R.id.nav_perfil -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, PerfilFragment())
                        .commit()
                    true
                }

                else -> false
            }
        }

    }

}