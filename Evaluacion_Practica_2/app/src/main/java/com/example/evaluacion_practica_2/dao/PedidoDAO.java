package com.example.evaluacion_practica_2.dao;
import androidx.room.*;
import com.example.evaluacion_practica_2.modelos.Pedido;
import java.util.List;

@Dao
public interface PedidoDAO {
    @Insert
    long insertar(Pedido p);
    @Update
    void actualizar(Pedido p);
    @Delete
    void eliminar(Pedido p);
    @Query("SELECT * FROM pedidos ORDER BY id DESC")
    List<Pedido> obtenerTodos();
    @Query("SELECT * FROM pedidos WHERE fecha = :fecha")
    List<Pedido> obtenerPorFecha(String fecha);
    @Query("SELECT COALESCE(SUM(total), 0) FROM pedidos WHERE estado = 'completado' AND fecha = :fecha")
    double totalVentasDia(String fecha);
    @Query("SELECT COUNT(*) FROM pedidos WHERE estado = 'completado' AND fecha = :fecha")
    int contarVentasDia(String fecha);
    @Query("SELECT COUNT(*) FROM pedidos WHERE estado = 'pendiente'")
    int contarPendientes();
}
