package com.example.evaluacion_practica_2.modelos;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "pedido_detalles")
public class PedidoDetalle {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int pedidoId;
    public int productoId;
    public String productoNombre;
    public int cantidad;
    public double precioUnitario;
    public double subtotal;
}
