package com.example.lab_inventory

import android.annotation.SuppressLint
import android.os.Bundle
import android.content.Intent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.lab_inventory.databinding.ActivityHomeBinding
import com.google.firebase.auth.FirebaseAuth
import androidx.activity.OnBackPressedCallback

enum class ProviderType {
    BASIC
}

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Inicializar View Binding
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar las vistas con información de la actividad anterior
        val email = intent.getStringExtra("email") ?: ""
        val sesion = "Iniciaste sesion como $email"
        binding.emailTextView.text = sesion

        // Configurar el comportamiento de cierre de sesión
        binding.logOutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            finish() // Cierra la actividad después de cerrar sesión
        }

        // Configurar el comportamiento del botón de retroceso
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    FirebaseAuth.getInstance().signOut()
                    finish()
                }
            }
        )

        // Configurar la navegación hacia CrudLabActivity con la lógica de clics
        binding.btnLab1.setOnClickListener {
            navigateToCrudLab("Lab1")
        }

        binding.btnLab2.setOnClickListener {
            navigateToCrudLab("Lab2")
        }

        binding.btnLab3.setOnClickListener {
            navigateToCrudLab("Lab3")
        }

        binding.btnLab4.setOnClickListener {
            navigateToCrudLab("Lab4")
        }
    }

    /**
     * Función para navegar hacia la actividad CrudLabActivity
     * y enviar información sobre el laboratorio correspondiente.
     */
    private fun navigateToCrudLab(labType: String) {
        val intent = Intent(this, CrudLabActivity::class.java)
        intent.putExtra("labType", labType) // Enviar información del laboratorio
        startActivity(intent)
    }
}

