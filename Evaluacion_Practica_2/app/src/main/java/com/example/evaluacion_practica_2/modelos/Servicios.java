package com.example.evaluacion_practica_2.modelos;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "servicios",
        foreignKeys = @ForeignKey(
                entity = Clientes.class,
                parentColumns = "id",
                childColumns = "cliente_id", onDelete = ForeignKey.CASCADE
        ))
public class Servicios {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "cliente_id")
    private int clienteId;
    @ColumnInfo(name = "tipo_servicio")
    private String tipoServicio;
    private String estado;
    private String descripcion;
    @ColumnInfo(name = "costo_mano_obra")
    private double costoManoObra;
    @ColumnInfo(name = "costo_materiales")
    private double costoMateriales;

    public Servicios() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getClienteId() {
        return clienteId;
    }

    public void setClienteId(int clienteId) {
        this.clienteId = clienteId;
    }

    public String getTipoServicio() {
        return tipoServicio;
    }

    public void setTipoServicio(String tipoServicio) {
        this.tipoServicio = tipoServicio;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getCostoManoObra() {
        return costoManoObra;
    }

    public void setCostoManoObra(double costoManoObra) {
        this.costoManoObra = costoManoObra;
    }

    public double getCostoMateriales() {
        return costoMateriales;
    }

    public void setCostoMateriales(double costoMateriales) {
        this.costoMateriales = costoMateriales;
    }
}
