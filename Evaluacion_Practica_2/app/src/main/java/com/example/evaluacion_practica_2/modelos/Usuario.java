package com.example.evaluacion_practica_2.modelos;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "usuarios")
public class Usuario {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String nombre;
    public String usuario;
    public String contrasena;
    public String rol; // "admin" o "cajero"
}
