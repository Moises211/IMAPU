package com.example.evaluacion_practica_2;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.evaluacion_practica_2.data.AppDB;
import com.example.evaluacion_practica_2.modelos.Usuario;

public class RegisterActivity extends AppCompatActivity {
    private EditText etNombre, etUsuario, etContrasena, etConfirmar;
    private RadioGroup rgRol;
    private AppDB db;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar toolbar = findViewById(R.id.toolbar_register);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        db = AppDB.getInstance(this);
        session = new SessionManager(this);
        etNombre = findViewById(R.id.et_nombre);
        etUsuario = findViewById(R.id.et_usuario_reg);
        etContrasena = findViewById(R.id.et_contrasena_reg);
        etConfirmar = findViewById(R.id.et_confirmar);
        rgRol = findViewById(R.id.rg_rol);
        if (!session.isAdmin()) {
            rgRol.check(R.id.rb_cajero);
            rgRol.setVisibility(View.GONE);
        }

        Button btnRegistrar = findViewById(R.id.btn_registrar);
        btnRegistrar.setOnClickListener(v -> registrar());
    }

    private void registrar() {
        String nombre = etNombre.getText().toString().trim();
        String usuario = etUsuario.getText().toString().trim();
        String pass = etContrasena.getText().toString().trim();
        String confirmar = etConfirmar.getText().toString().trim();

        if (nombre.isEmpty() || usuario.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!pass.equals(confirmar)) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }
        if (db.usuarioDAO().buscarPorUsuario(usuario) != null) {
            Toast.makeText(this, "Ese nombre de usuario ya existe", Toast.LENGTH_SHORT).show();
            return;
        }

        String rol = session.isAdmin() && rgRol.getCheckedRadioButtonId() == R.id.rb_admin ? "admin" : "cajero";
        Usuario u = new Usuario();
        u.nombre = nombre;
        u.usuario = usuario;
        u.contrasena = pass;
        u.rol = rol;
        db.usuarioDAO().insertar(u);
        Toast.makeText(this, "Cuenta creada exitosamente", Toast.LENGTH_SHORT).show();
        finish();
    }
}
