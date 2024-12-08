package com.example.lab_inventory

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.lab_inventory.databinding.ActivityAuthBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar View Binding
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // Manejar el botón de Registro
        binding.singUpButton.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()

            registerUser(email, password)
        }

        // Manejar el botón de Login
        binding.logInButton.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()

            loginUser(email, password)
        }
    }

    private fun limpiarFormulario(){
        binding.emailEditText.text.clear()
        binding.passwordEditText.text.clear()
    }

    private fun registerUser(email: String, password: String) {
        db.collection("users").add(mapOf("email" to email, "password" to password))
            .addOnSuccessListener {
                Toast.makeText(this, "Usuario registrado con exito", Toast.LENGTH_SHORT).show()
                limpiarFormulario()
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
            }
            .addOnFailureListener(){
                Toast.makeText(this, "Erorr al registrar usuario", Toast.LENGTH_SHORT).show()
            }

    }

    private fun loginUser(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()){ //si los campos estna vacios
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
        } else { //si los campos no estan vacios
            db.collection("users")
                .whereEqualTo("email", email)
                .whereEqualTo("password", password)
                .get() //Obtiene los resultados
                .addOnSuccessListener { documents ->
                    if (documents.isEmpty) {//Si no hay resultados muestra error
                        Toast.makeText(this, "Usuario o contraseña incorrecta", Toast.LENGTH_SHORT).show()
                    } else {
                        binding.emailEditText.text.clear()
                        binding.passwordEditText.text.clear()

                        val intent = Intent(this, HomeActivity::class.java)
                        intent.putExtra("email", email)
                        startActivity(intent)
                    }
                }
                .addOnFailureListener{ e -> //Error al conectarse con Firestore
                    Toast.makeText(this, "Eror al conectarse con Firebase: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}

