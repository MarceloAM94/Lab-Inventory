package com.example.lab_inventory

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ProductAdapter(
    private val products: List<Product>
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nombreItemTextView: TextView = view.findViewById(R.id.nombreItemTextView)
        val categoriaTextView: TextView = view.findViewById(R.id.categoriaTextView)
        val cantidadTextView: TextView = view.findViewById(R.id.cantidadTextView)
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
    }

    override fun getItemCount(): Int = products.size
}
