package com.example.evaluacion_practica_2.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.evaluacion_practica_2.dao.ClientesDAO;
import com.example.evaluacion_practica_2.dao.ServiciosDAO;
import com.example.evaluacion_practica_2.modelos.Clientes;
import com.example.evaluacion_practica_2.modelos.Servicios;

@Database(entities = {Clientes.class, Servicios.class}, version = 1)
public abstract class AppDB extends RoomDatabase {
    private static volatile AppDB instancia;

    public abstract ClientesDAO clientesDAO();
    public abstract ServiciosDAO serviciosDAO();

    public static synchronized AppDB getInstance(Context context){
        if(instancia == null){
            instancia = Room.databaseBuilder(context.getApplicationContext(), AppDB.class, "db_taller")
                    .build();
        }
        return instancia;
    }
}
