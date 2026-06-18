package com.example.evaluacion_practica_2.dao;
import androidx.room.*;
import com.example.evaluacion_practica_2.modelos.Usuario;
import java.util.List;

@Dao
public interface UsuarioDAO {
    @Insert
    void insertar(Usuario u);
    @Update
    void actualizar(Usuario u);
    @Delete
    void eliminar(Usuario u);
    @Query("SELECT * FROM usuarios")
    List<Usuario> obtenerTodos();
    @Query("SELECT * FROM usuarios WHERE usuario = :user AND contrasena = :pass LIMIT 1")
    Usuario login(String user, String pass);
    @Query("SELECT * FROM usuarios WHERE usuario = :user LIMIT 1")
    Usuario buscarPorUsuario(String user);
    @Query("SELECT * FROM usuarios WHERE id = :id LIMIT 1")
    Usuario buscarPorId(int id);
}
