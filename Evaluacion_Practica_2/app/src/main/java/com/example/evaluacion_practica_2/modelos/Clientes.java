package com.example.evaluacion_practica_2.modelos;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "clientes")
public class Clientes {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "nombres_completo")
    private String nombresCompleto;
    private String telefono;
    private String email;
    private String direccion;
    private String municipio;
    private String notas;

    public Clientes() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombresCompleto() {
        return nombresCompleto;
    }

    public void setNombresCompleto(String nombresCompleto) {
        this.nombresCompleto = nombresCompleto;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }
}
