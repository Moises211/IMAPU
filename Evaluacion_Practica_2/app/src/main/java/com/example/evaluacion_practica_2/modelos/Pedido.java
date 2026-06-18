package com.example.evaluacion_practica_2.modelos;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "pedidos")
public class Pedido {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int clienteId;
    public int productoId;
    public int cantidad;
    public double precioUnitario;
    public String descripcion;
    public double total;
    public String estado; // "pendiente", "completado", "cancelado"
    public String fecha;
    public String metodoPago; // "efectivo", "tarjeta"
}
