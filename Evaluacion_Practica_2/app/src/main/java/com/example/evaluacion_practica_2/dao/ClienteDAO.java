package com.example.evaluacion_practica_2.dao;
import androidx.room.*;
import com.example.evaluacion_practica_2.modelos.Cliente;
import java.util.List;

@Dao
public interface ClienteDAO {
    @Insert
    void insertar(Cliente c);
    @Update
    void actualizar(Cliente c);
    @Delete
    void eliminar(Cliente c);
    @Query("SELECT * FROM clientes")
    List<Cliente> obtenerTodos();
    @Query("SELECT * FROM clientes WHERE nombre LIKE '%' || :q || '%'")
    List<Cliente> buscar(String q);
    @Query("SELECT * FROM clientes WHERE tipo = :tipo")
    List<Cliente> obtenerPorTipo(String tipo);
}
