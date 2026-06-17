package com.example.evaluacion_practica_2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.evaluacion_practica_2.modelos.Clientes;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DetalleClienteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetalleClienteFragment extends Fragment {

    private Clientes cliente;
    private String nombre, telefono, email, direccion, municipio, notas;

    public DetalleClienteFragment() {

    }


    public static DetalleClienteFragment newInstance(Clientes c) {
        DetalleClienteFragment fragment = new DetalleClienteFragment();
        Bundle args = new Bundle();
        System.out.println("Cliente: " + c.getNombresCompleto());
        args.putString("det_nombre", c.getNombresCompleto());
        args.putString("det_telefono", c.getTelefono());
        args.putString("det_email", c.getEmail());
        args.putString("det_direccion", c.getDireccion());
        args.putString("det_municipio", c.getMunicipio());
        args.putString("det_notas", c.getNotas());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            nombre = getArguments().getString("det_nombre");
            System.out.println("Cliente: 2 " + nombre);
            telefono = getArguments().getString("det_telefono");
            email = getArguments().getString("det_email");
            direccion = getArguments().getString("det_direccion");
            municipio = getArguments().getString("det_municipio");
            notas = getArguments().getString("det_notas");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detalle_cliente, container, false);

        //if (cliente == null) return view;

        TextView tvNombre = view.findViewById(R.id.tv_deta_cliente_nombre);
        TextView tvTelefono = view.findViewById(R.id.tv_det_cliente_telefono);
        TextView tvEmail = view.findViewById(R.id.tv_det_cliente_email);
        TextView tvDireccion = view.findViewById(R.id.tv_det_cliente_direccion);
        TextView tvMunicipio = view.findViewById(R.id.tv_det_cliente_municipio);
        TextView tvNotas = view.findViewById(R.id.tv_det_cliente_notas);
        //Button btnLlamar = view.findViewById(R.id.btn_det_cliente_llamar);
        //Button btnWhatsapp = view.findViewById(R.id.btn_det_cliente_whatsapp);

        tvNombre.setText(nombre);
        tvTelefono.setText(telefono);
        tvEmail.setText(email);
        tvDireccion.setText(direccion);
        System.out.println("Cliente: 3 " + tvNombre.getText());
        tvMunicipio.setText(municipio);
        tvNotas.setText(notas != null && !notas.isBlank() ? notas : "Sin notas");

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() instanceof AppCompatActivity) {
            ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (actionBar != null) {

                actionBar.setTitle(nombre != null ? nombre : "Detalle de Cliente");

                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (getActivity() instanceof AppCompatActivity) {
            ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(false);
            }
        }
    }
}