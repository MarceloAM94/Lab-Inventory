package com.example.lab_inventory

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ProductAdapter(
    private val products: List<Product>,
    private val onDeleteClicked: (Product) -> Unit, // Callback para manejar clics en eliminar
    private val onEditClicked: (Product) -> Unit // Callback para manejar clics en editar
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nombreItemTextView: TextView = view.findViewById(R.id.nombreItemTextView)
        val categoriaTextView: TextView = view.findViewById(R.id.categoriaTextView)
        val cantidadTextView: TextView = view.findViewById(R.id.cantidadTextView)
        val btnEliminar: Button = view.findViewById(R.id.btnEliminarProducto) // Botón de eliminar
        val btnEditar: Button = view.findViewById(R.id.btnEditarProducto) // Botón de editar
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        holder.nombreItemTextView.text = product.nombre_item
        holder.categoriaTextView.text = "Categoría: ${product.categoria}"
        holder.cantidadTextView.text = "Cantidad: ${product.cantidad}"

        // Configurar el botón de eliminar
        holder.btnEliminar.setOnClickListener {
            onDeleteClicked(product) // Llamar al callback con el producto para eliminar
        }

        // Configurar el botón de editar
        holder.btnEditar.setOnClickListener {
            onEditClicked(product) // Llamar al callback con el producto para editar
        }
    }

    override fun getItemCount(): Int = products.size
}

