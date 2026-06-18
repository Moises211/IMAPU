package com.example.evaluacion_practica_2.data;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.example.evaluacion_practica_2.dao.*;
import com.example.evaluacion_practica_2.modelos.*;

@Database(entities = {Usuario.class, Producto.class, Cliente.class, Pedido.class}, version = 2, exportSchema = false)
public abstract class AppDB extends RoomDatabase {
    private static AppDB instancia;

    public abstract UsuarioDAO usuarioDAO();
    public abstract ProductoDAO productoDAO();
    public abstract ClienteDAO clienteDAO();
    public abstract PedidoDAO pedidoDAO();

    public static synchronized AppDB getInstance(Context context) {
        if (instancia == null) {
            instancia = Room.databaseBuilder(context.getApplicationContext(), AppDB.class, "minisuper_db")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
        }
        return instancia;
    }
}
