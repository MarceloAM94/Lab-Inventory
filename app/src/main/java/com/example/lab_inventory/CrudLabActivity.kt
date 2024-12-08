package com.example.lab_inventory

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lab_inventory.databinding.ActivityCrudLabBinding
import com.google.firebase.firestore.FirebaseFirestore

class CrudLabActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCrudLabBinding
    private val db = FirebaseFirestore.getInstance()
    private val products = mutableListOf<Product>()
    private lateinit var adapter: ProductAdapter
    private lateinit var labId: String // Declaración de la variable de clase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCrudLabBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtener el ID del laboratorio desde el Intent y asignar a la variable de clase
        labId = intent.getStringExtra("labType") ?: ""

        // Configurar título basado en el laboratorio
        binding.txtLabTitle.text = when (labId) {
            "Lab1" -> "Laboratorio 1"
            "Lab2" -> "Laboratorio 2"
            "Lab3" -> "Laboratorio 3"
            "Lab4" -> "Laboratorio 4"
            else -> "Laboratorio Desconocido"
        }

        // Configurar RecyclerView
        setupRecyclerView()

        // Cargar datos de Firestore
        loadInventoryData(labId)

        volverHome()

        binding.btnAgregar.setOnClickListener {
            showAddProductDialog()
        }
    }

    private fun setupRecyclerView() {
        adapter = ProductAdapter(products)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@CrudLabActivity)
            adapter = this@CrudLabActivity.adapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
    }

    private fun loadInventoryData(labId: String) {
        db.collection("inventario")
            .whereEqualTo("ID_laboratorios", labId)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    products.clear() // Limpia la lista para evitar duplicados
                    for (document in documents) {
                        val item = document.toObject(Product::class.java)
                        products.add(item)
                    }
                    adapter.notifyDataSetChanged() // Notifica al adaptador que los datos han cambiado
                } else {
                    Log.d("CrudLabActivity", "No se encontraron productos para el laboratorio: $labId")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("CrudLabActivity", "Error al cargar inventario: ", exception)
            }
    }

    private fun volverHome() {
        binding.btnMenu.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun showAddProductDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_product, null)
        val dialog = AlertDialog.Builder(this)
            .setTitle("Agregar Producto")
            .setView(dialogView)
            .setPositiveButton("Guardar") { _, _ ->
                val idLaboratorios = dialogView.findViewById<EditText>(R.id.etIdLaboratorios).text.toString()
                val nombreItem = dialogView.findViewById<EditText>(R.id.etNombre).text.toString()
                val categoria = dialogView.findViewById<EditText>(R.id.etCategoria).text.toString()
                val cantidadString = dialogView.findViewById<EditText>(R.id.etCantidad).text.toString()
                val estado = dialogView.findViewById<EditText>(R.id.etEstado).text.toString()

                val cantidad = cantidadString.toIntOrNull() ?: 0

                val newProduct = hashMapOf(
                    "ID_laboratorios" to idLaboratorios, // Asegúrate de enviar la variable de clase aquí
                    "nombre_item" to nombreItem,
                    "categoria" to categoria,
                    "cantidad" to cantidad,
                    "estado" to estado
                )

                db.collection("inventario")
                    .document()
                    .set(newProduct)
                    .addOnSuccessListener {
                        Log.d("CrudLabActivity", "Producto agregado correctamente")
                        Toast.makeText(this, "Producto guardado", Toast.LENGTH_SHORT).show()
                        loadInventoryData(labId)
                    }
                    .addOnFailureListener { e ->
                        Log.e("CrudLabActivity", "Error al guardar el producto", e)
                        Toast.makeText(this, "Error al guardar el producto", Toast.LENGTH_SHORT).show()
                    }
            }
            .setNegativeButton("Cancelar", null)
            .create()
        dialog.show()
    }
}

