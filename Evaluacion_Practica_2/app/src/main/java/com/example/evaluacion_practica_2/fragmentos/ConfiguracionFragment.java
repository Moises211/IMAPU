package com.example.evaluacion_practica_2.fragmentos;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import com.example.evaluacion_practica_2.LoginActivity;
import com.example.evaluacion_practica_2.R;
import com.example.evaluacion_practica_2.SessionManager;

public class ConfiguracionFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_configuracion, container, false);
        SessionManager session = new SessionManager(requireContext());

        TextView tvNombre = view.findViewById(R.id.tv_config_nombre);
        TextView tvUsuario = view.findViewById(R.id.tv_config_usuario);
        TextView tvRol = view.findViewById(R.id.tv_config_rol);
        Switch switchTema = view.findViewById(R.id.switch_tema);
        Button btnCerrarSesion = view.findViewById(R.id.btn_cerrar_sesion);

        tvNombre.setText("Nombre: " + session.getNombre());
        tvUsuario.setText("Usuario: " + session.getUsuario());
        tvRol.setText("Rol: " + session.getRol());

        switchTema.setChecked(session.isTemaOscuro());
        switchTema.setOnCheckedChangeListener((btn, isChecked) -> {
            session.setTemaOscuro(isChecked);
            AppCompatDelegate.setDefaultNightMode(isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
            requireActivity().recreate();
        });

        btnCerrarSesion.setOnClickListener(v -> {
            session.cerrarSesion();
            startActivity(new Intent(requireContext(), LoginActivity.class));
            requireActivity().finish();
        });

        return view;
    }
}
