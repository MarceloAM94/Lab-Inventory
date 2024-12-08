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
        adapter = ProductAdapter(
            products,
            onDeleteClicked = { product -> // Callback para eliminar
                showDeleteConfirmationDialog(product)
            },
            onEditClicked = { product -> // Callback para editar
                showEditProductDialog(product)
            }
        )
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
                    products.clear()
                    for (document in documents) {
                        val item = document.toObject(Product::class.java).apply {
                            ID_Document = document.id // Asignar el ID del documento al modelo
                        }
                        products.add(item)
                    }
                    adapter.notifyDataSetChanged()
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

    //Anadir producto
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

    //Confirmacion para eliminar producto
    private fun showDeleteConfirmationDialog(product: Product) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar Producto")
            .setMessage("¿Estás seguro de que deseas eliminar el producto '${product.nombre_item}'?")
            .setPositiveButton("Eliminar") { _, _ ->
                deleteProductFromFirestore(product)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun deleteProductFromFirestore(product: Product) {
        db.collection("inventario")
            .document(product.ID_Document) // El `id` debe ser el identificador único del documento
            .delete()
            .addOnSuccessListener {
                Log.d("CrudLabActivity", "Producto eliminado correctamente")
                Toast.makeText(this, "Producto eliminado", Toast.LENGTH_SHORT).show()

                // Actualizar la lista de productos
                products.remove(product)
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Log.e("CrudLabActivity", "Error al eliminar el producto", e)
                Toast.makeText(this, "Error al eliminar el producto", Toast.LENGTH_SHORT).show()
            }
    }

    //Dialogo para editar producto
    private fun showEditProductDialog(product: Product) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_product, null)

        val etNombre = dialogView.findViewById<EditText>(R.id.etNombre)
        val etCategoria = dialogView.findViewById<EditText>(R.id.etCategoria)
        val etCantidad = dialogView.findViewById<EditText>(R.id.etCantidad)
        val etEstado = dialogView.findViewById<EditText>(R.id.etEstado)

        // Precargar los datos del producto
        etNombre.setText(product.nombre_item)
        etCategoria.setText(product.categoria)
        etCantidad.setText(product.cantidad.toString())
        etEstado.setText(product.estado)

        val dialog = AlertDialog.Builder(this)
            .setTitle("Editar Producto")
            .setView(dialogView)
            .setPositiveButton("Guardar") { _, _ ->
                val nombreItem = etNombre.text.toString()
                val categoria = etCategoria.text.toString()
                val cantidad = etCantidad.text.toString().toIntOrNull() ?: 0
                val estado = etEstado.text.toString()

                // Crear el mapa directamente como MutableMap<String, Any>
                val updatedProduct: MutableMap<String, Any> = mutableMapOf(
                    "nombre_item" to nombreItem,
                    "categoria" to categoria,
                    "cantidad" to cantidad,
                    "estado" to estado
                )

                // Actualizar el producto en Firestore
                db.collection("inventario").document(product.ID_Document)
                    .update(updatedProduct)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Producto actualizado", Toast.LENGTH_SHORT).show()
                        loadInventoryData(labId) // Recargar la lista
                    }
                    .addOnFailureListener { e ->
                        Log.e("CrudLabActivity", "Error al actualizar producto", e)
                        Toast.makeText(this, "Error al actualizar: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .setNegativeButton("Cancelar", null)
            .create()
        dialog.show()
    }


}

