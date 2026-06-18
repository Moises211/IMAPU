package com.example.evaluacion_practica_2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import com.example.evaluacion_practica_2.data.AppDB;
import com.example.evaluacion_practica_2.modelos.Usuario;

public class LoginActivity extends AppCompatActivity {
    private EditText etUsuario, etContrasena;
    private AppDB db;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        session = new SessionManager(this);
        if (session.isTemaOscuro()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        super.onCreate(savedInstanceState);

        if (session.estaLogueado()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_login);
        db = AppDB.getInstance(this);

        // Crear usuario admin por defecto si la BD está vacía
        if (db.usuarioDAO().obtenerTodos().isEmpty()) {
            Usuario admin = new Usuario();
            admin.nombre = "Administrador";
            admin.usuario = "admin";
            admin.contrasena = "admin123";
            admin.rol = "admin";
            db.usuarioDAO().insertar(admin);
        }

        etUsuario = findViewById(R.id.et_usuario);
        etContrasena = findViewById(R.id.et_contrasena);
        Button btnLogin = findViewById(R.id.btn_login);
        TextView tvRegistrar = findViewById(R.id.tv_registrar);

        btnLogin.setOnClickListener(v -> {
            String user = etUsuario.getText().toString().trim();
            String pass = etContrasena.getText().toString().trim();
            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }
            Usuario u = db.usuarioDAO().login(user, pass);
            if (u != null) {
                session.guardarSesion(u.usuario, u.nombre, u.rol);
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
            }
        });

        tvRegistrar.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
    }
}
