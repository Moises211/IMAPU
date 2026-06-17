package com.example.evaluacion_practica_2.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.evaluacion_practica_2.modelos.Clientes;

import java.util.List;

@Dao
public interface ClientesDAO {
    @Insert
    void crear(Clientes clientes);
    @Update
    void actualizar(Clientes clientes);
    @Delete
    void eliminar(Clientes clientes);
    @Query("SELECT * FROM clientes ORDER BY nombres_completo ASC")
    List<Clientes> obtenerClientes();
    @Query("SELECT * FROM clientes WHERE nombres_completo LIKE :buscarNombre")
    List<Clientes> buscarPorNombre(String buscarNombre);
    @Query("SELECT * FROM clientes WHERE id = :clienteId LIMIT 1")
    Clientes obtenerClientePorId(int clienteId);

    @Query("SELECT COUNT(*) FROM clientes")
    int contarTotalClientes();
}
