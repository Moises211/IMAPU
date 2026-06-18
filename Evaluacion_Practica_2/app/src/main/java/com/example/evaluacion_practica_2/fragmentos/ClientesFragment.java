package com.example.evaluacion_practica_2.fragmentos;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.evaluacion_practica_2.R;
import com.example.evaluacion_practica_2.adapter.ClienteAdapter;
import com.example.evaluacion_practica_2.data.AppDB;
import com.example.evaluacion_practica_2.modelos.Cliente;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;

public class ClientesFragment extends Fragment {
    private AppDB db;
    private ClienteAdapter adapter;
    private List<Cliente> lista;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_clientes, container, false);
        db = AppDB.getInstance(requireContext());

        RecyclerView rv = view.findViewById(R.id.rv_clientes);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        lista = db.clienteDAO().obtenerTodos();
        adapter = new ClienteAdapter(lista, this::onClienteLongClick);
        rv.setAdapter(adapter);

        EditText etBuscar = view.findViewById(R.id.et_buscar_cliente);
        etBuscar.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                lista.clear();
                lista.addAll(s.toString().isEmpty() ? db.clienteDAO().obtenerTodos() : db.clienteDAO().buscar(s.toString()));
                adapter.notifyDataSetChanged();
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        FloatingActionButton fab = view.findViewById(R.id.fab_nuevo_cliente);
        fab.setOnClickListener(v -> mostrarDialogoCliente(null));

        return view;
    }

    private void onClienteLongClick(Cliente c) {
        String[] opciones = {"Editar", "Eliminar"};
        new AlertDialog.Builder(requireContext())
                .setTitle(c.nombre)
                .setItems(opciones, (dialog, which) -> {
                    if (which == 0) mostrarDialogoCliente(c);
                    else confirmarEliminar(c);
                }).show();
    }

    private void confirmarEliminar(Cliente c) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Eliminar")
                .setMessage("¿Eliminar a " + c.nombre + "?")
                .setPositiveButton("Sí", (d, w) -> { db.clienteDAO().eliminar(c); refrescar(); })
                .setNegativeButton("No", null).show();
    }

    private void mostrarDialogoCliente(Cliente existente) {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_cliente, null);
        EditText etNombre = dialogView.findViewById(R.id.et_nombre_cli);
        EditText etTelefono = dialogView.findViewById(R.id.et_telefono_cli);
        EditText etDireccion = dialogView.findViewById(R.id.et_direccion_cli);
        EditText etNotas = dialogView.findViewById(R.id.et_notas_cli);
        Spinner spTipo = dialogView.findViewById(R.id.sp_tipo_cli);

        String[] tipos = {"cliente", "proveedor"};
        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, tipos);
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTipo.setAdapter(adapterSpinner);

        if (existente != null) {
            etNombre.setText(existente.nombre);
            etTelefono.setText(existente.telefono);
            etDireccion.setText(existente.direccion);
            etNotas.setText(existente.notas);
            spTipo.setSelection(existente.tipo.equals("proveedor") ? 1 : 0);
        }

        new AlertDialog.Builder(requireContext())
                .setTitle(existente == null ? "Nuevo Cliente/Proveedor" : "Editar")
                .setView(dialogView)
                .setPositiveButton("Guardar", (d, w) -> {
                    String nombre = etNombre.getText().toString().trim();
                    if (nombre.isEmpty()) { Toast.makeText(requireContext(), "Nombre requerido", Toast.LENGTH_SHORT).show(); return; }
                    Cliente c = existente != null ? existente : new Cliente();
                    c.nombre = nombre;
                    c.telefono = etTelefono.getText().toString().trim();
                    c.direccion = etDireccion.getText().toString().trim();
                    c.notas = etNotas.getText().toString().trim();
                    c.tipo = spTipo.getSelectedItem().toString();
                    if (existente == null) db.clienteDAO().insertar(c);
                    else db.clienteDAO().actualizar(c);
                    refrescar();
                })
                .setNegativeButton("Cancelar", null).show();
    }

    private void refrescar() {
        lista.clear();
        lista.addAll(db.clienteDAO().obtenerTodos());
        adapter.notifyDataSetChanged();
    }
}
