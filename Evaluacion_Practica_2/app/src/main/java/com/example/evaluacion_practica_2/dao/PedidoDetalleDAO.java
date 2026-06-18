package com.example.evaluacion_practica_2.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.example.evaluacion_practica_2.modelos.PedidoDetalle;
import java.util.List;

@Dao
public interface PedidoDetalleDAO {
    @Insert
    void insertar(PedidoDetalle detalle);

    @Query("SELECT * FROM pedido_detalles WHERE pedidoId = :pedidoId")
    List<PedidoDetalle> obtenerPorPedido(int pedidoId);

    @Query("DELETE FROM pedido_detalles WHERE pedidoId = :pedidoId")
    void eliminarPorPedido(int pedidoId);
}
