package com.example.evaluacion_practica_2.fragmentos;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.evaluacion_practica_2.LoginActivity;
import com.example.evaluacion_practica_2.R;
import com.example.evaluacion_practica_2.SessionManager;
import com.example.evaluacion_practica_2.adapter.UsuarioAdapter;
import com.example.evaluacion_practica_2.data.AppDB;
import com.example.evaluacion_practica_2.modelos.Usuario;
import java.util.List;

public class ConfiguracionFragment extends Fragment {
    private AppDB db;
    private UsuarioAdapter usuarioAdapter;
    private List<Usuario> usuarios;
    private SessionManager session;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_configuracion, container, false);
        session = new SessionManager(requireContext());
        db = AppDB.getInstance(requireContext());

        TextView tvNombre = view.findViewById(R.id.tv_config_nombre);
        TextView tvUsuario = view.findViewById(R.id.tv_config_usuario);
        TextView tvRol = view.findViewById(R.id.tv_config_rol);
        Switch switchTema = view.findViewById(R.id.switch_tema);
        Button btnCerrarSesion = view.findViewById(R.id.btn_cerrar_sesion);
        Button btnNuevoUsuario = view.findViewById(R.id.btn_nuevo_usuario);
        LinearLayout layoutAdminUsuarios = view.findViewById(R.id.layout_admin_usuarios);
        RecyclerView rvUsuarios = view.findViewById(R.id.rv_usuarios);

        tvNombre.setText("Nombre: " + session.getNombre());
        tvUsuario.setText("Usuario: " + session.getUsuario());
        tvRol.setText("Rol: " + session.getRol());

        switchTema.setChecked(session.isTemaOscuro());
        switchTema.setOnCheckedChangeListener((btn, isChecked) -> {
            session.setTemaOscuro(isChecked);
            AppCompatDelegate.setDefaultNightMode(isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
            requireActivity().recreate();
        });

        layoutAdminUsuarios.setVisibility(session.isAdmin() ? View.VISIBLE : View.GONE);
        if (session.isAdmin()) {
            rvUsuarios.setLayoutManager(new LinearLayoutManager(requireContext()));
            usuarios = db.usuarioDAO().obtenerTodos();
            usuarioAdapter = new UsuarioAdapter(usuarios, this::onUsuarioLongClick);
            rvUsuarios.setAdapter(usuarioAdapter);
            btnNuevoUsuario.setOnClickListener(v -> mostrarDialogoUsuario(null));
        }

        btnCerrarSesion.setOnClickListener(v -> {
            session.cerrarSesion();
            startActivity(new Intent(requireContext(), LoginActivity.class));
            requireActivity().finish();
        });

        return view;
    }

    private void onUsuarioLongClick(Usuario usuario) {
        String[] opciones = {"Editar", "Eliminar"};
        new AlertDialog.Builder(requireContext())
                .setTitle(usuario.nombre)
                .setItems(opciones, (dialog, which) -> {
                    if (which == 0) mostrarDialogoUsuario(usuario);
                    else confirmarEliminar(usuario);
                }).show();
    }

    private void confirmarEliminar(Usuario usuario) {
        if (session.getUsuario().equals(usuario.usuario)) {
            Toast.makeText(requireContext(), "No puede eliminar su propio usuario", Toast.LENGTH_SHORT).show();
            return;
        }
        new AlertDialog.Builder(requireContext())
                .setTitle("Eliminar usuario")
                .setMessage("¿Eliminar a " + usuario.nombre + "?")
                .setPositiveButton("Sí", (d, w) -> {
                    db.usuarioDAO().eliminar(usuario);
                    refrescarUsuarios();
                })
                .setNegativeButton("No", null).show();
    }

    private void mostrarDialogoUsuario(Usuario existente) {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_usuario, null);
        EditText etNombre = dialogView.findViewById(R.id.et_usuario_nombre);
        EditText etUsuario = dialogView.findViewById(R.id.et_usuario_login);
        EditText etPassword = dialogView.findViewById(R.id.et_usuario_password);
        Spinner spRol = dialogView.findViewById(R.id.sp_usuario_rol);

        String[] roles = {"cajero", "admin"};
        ArrayAdapter<String> rolAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, roles);
        rolAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRol.setAdapter(rolAdapter);

        if (existente != null) {
            etNombre.setText(existente.nombre);
            etUsuario.setText(existente.usuario);
            etPassword.setText(existente.contrasena);
            spRol.setSelection("admin".equals(existente.rol) ? 1 : 0);
        }

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle(existente == null ? "Nuevo usuario" : "Editar usuario")
                .setView(dialogView)
                .setPositiveButton("Guardar", null)
                .setNegativeButton("Cancelar", null)
                .create();

        dialog.setOnShowListener(d -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();
            String usuarioLogin = etUsuario.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String rol = spRol.getSelectedItem().toString();

            if (nombre.isEmpty() || usuarioLogin.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Complete todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            Usuario existenteConUsuario = db.usuarioDAO().buscarPorUsuario(usuarioLogin);
            if (existenteConUsuario != null && (existente == null || existenteConUsuario.id != existente.id)) {
                Toast.makeText(requireContext(), "Ese nombre de usuario ya existe", Toast.LENGTH_SHORT).show();
                return;
            }

            Usuario u = existente != null ? existente : new Usuario();
            u.nombre = nombre;
            u.usuario = usuarioLogin;
            u.contrasena = password;
            u.rol = rol;

            if (existente == null) db.usuarioDAO().insertar(u);
            else db.usuarioDAO().actualizar(u);

            refrescarUsuarios();
            dialog.dismiss();
        }));
        dialog.show();
    }

    private void refrescarUsuarios() {
        usuarios.clear();
        usuarios.addAll(db.usuarioDAO().obtenerTodos());
        usuarioAdapter.notifyDataSetChanged();
    }
}
