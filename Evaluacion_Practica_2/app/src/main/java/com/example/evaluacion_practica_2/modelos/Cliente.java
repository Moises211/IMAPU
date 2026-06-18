package com.example.evaluacion_practica_2.modelos;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "clientes")
public class Cliente {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String nombre;
    public String telefono;
    public String tipo; // "cliente" o "proveedor"
    public String direccion;
    public String notas;
}
