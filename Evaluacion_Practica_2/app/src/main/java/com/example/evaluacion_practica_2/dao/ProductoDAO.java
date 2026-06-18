package com.example.evaluacion_practica_2.dao;
import androidx.room.*;
import com.example.evaluacion_practica_2.modelos.Producto;
import java.util.List;

@Dao
public interface ProductoDAO {
    @Insert
    void insertar(Producto p);
    @Update
    void actualizar(Producto p);
    @Delete
    void eliminar(Producto p);
    @Query("SELECT * FROM productos")
    List<Producto> obtenerTodos();
    @Query("SELECT * FROM productos WHERE nombre LIKE '%' || :q || '%' OR categoria LIKE '%' || :q || '%'")
    List<Producto> buscar(String q);
    @Query("SELECT * FROM productos WHERE stock <= 5")
    List<Producto> stockBajo();
    @Query("SELECT COUNT(*) FROM productos")
    int contarProductos();
}
