package com.example.evaluacion_practica_2.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.evaluacion_practica_2.modelos.Servicios;

import java.util.List;

@Dao
public interface ServiciosDAO {
    @Insert
    void crear(Servicios servicios);
    @Update
    void actualizar(Servicios servicios);
    @Delete
    void eliminar(Servicios servicios);
    @Query("SELECT * FROM servicios")
    List<Servicios> obtenerTodosLoServicios();
    @Query("SELECT * FROM servicios WHERE cliente_id = :clienteId")
    List<Servicios> obtenerServiciosPorCliente(int clienteId);
    @Query("SELECT * FROM servicios WHERE estado LIKE :estadoFiltro")
    List<Servicios> obtenerServiciosPorEstado(String estadoFiltro);

    @Query("SELECT * FROM servicios WHERE tipo_servicio = :tipoFiltro")
    List<Servicios> obtenerServiciosPorTipo(String tipoFiltro);

    @Query("SELECT COUNT(*) FROM servicios WHERE estado = 'Pendiente'")
    int contarServiciosPendientes();

    @Query("SELECT SUM(costo_mano_obra + costo_materiales) FROM servicios WHERE estado = 'Completado'")
    double calcularIngresosCompletados();
}
