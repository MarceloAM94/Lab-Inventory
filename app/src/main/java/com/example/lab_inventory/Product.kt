package com.example.lab_inventory

data class Product(
    val ID_Document: String = "",
    val ID_laboratorios: String = "",
    val nombre_item: String = "",
    val categoria: String = "",
    val cantidad: Int = 0,
    val estado: String = ""
)
