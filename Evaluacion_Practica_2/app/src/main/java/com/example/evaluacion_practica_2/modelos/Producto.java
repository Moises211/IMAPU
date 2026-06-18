package com.example.evaluacion_practica_2.modelos;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "productos")
public class Producto {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String nombre;
    public String categoria;
    public double precio;
    public int stock;
    public String codigoBarras;
}
