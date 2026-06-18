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
import com.example.evaluacion_practica_2.SessionManager;
import com.example.evaluacion_practica_2.adapter.ProductoAdapter;
import com.example.evaluacion_practica_2.data.AppDB;
import com.example.evaluacion_practica_2.modelos.Producto;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;

public class ProductosFragment extends Fragment {
    private AppDB db;
    private ProductoAdapter adapter;
    private List<Producto> lista;
    private boolean esAdmin;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_productos, container, false);
        db = AppDB.getInstance(requireContext());
        esAdmin = new SessionManager(requireContext()).isAdmin();

        RecyclerView rv = view.findViewById(R.id.rv_productos);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));

        lista = db.productoDAO().obtenerTodos();
        adapter = new ProductoAdapter(lista, this::onProductoLongClick);
        rv.setAdapter(adapter);

        EditText etBuscar = view.findViewById(R.id.et_buscar_producto);
        etBuscar.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                lista.clear();
                lista.addAll(s.toString().isEmpty() ? db.productoDAO().obtenerTodos() : db.productoDAO().buscar(s.toString()));
                adapter.notifyDataSetChanged();
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        FloatingActionButton fab = view.findViewById(R.id.fab_nuevo_producto);
        fab.setVisibility(esAdmin ? View.VISIBLE : View.GONE);
        fab.setOnClickListener(v -> {
            if (esAdmin) mostrarDialogoProducto(null);
            else mostrarSinPermiso();
        });

        return view;
    }

    private void onProductoLongClick(Producto p) {
        if (!esAdmin) {
            mostrarSinPermiso();
            return;
        }
        String[] opciones = {"Editar", "Eliminar"};
        new AlertDialog.Builder(requireContext())
                .setTitle(p.nombre)
                .setItems(opciones, (dialog, which) -> {
                    if (which == 0) mostrarDialogoProducto(p);
                    else confirmarEliminar(p);
                }).show();
    }

    private void mostrarSinPermiso() {
        Toast.makeText(requireContext(), "Solo el administrador puede modificar productos", Toast.LENGTH_SHORT).show();
    }

    private void confirmarEliminar(Producto p) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Eliminar producto")
                .setMessage("¿Eliminar " + p.nombre + "?")
                .setPositiveButton("Sí", (d, w) -> {
                    db.productoDAO().eliminar(p);
                    refrescar();
                })
                .setNegativeButton("No", null).show();
    }

    private void mostrarDialogoProducto(Producto existente) {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_producto, null);
        EditText etNombre = dialogView.findViewById(R.id.et_nombre_prod);
        EditText etCategoria = dialogView.findViewById(R.id.et_categoria);
        EditText etPrecio = dialogView.findViewById(R.id.et_precio);
        EditText etStock = dialogView.findViewById(R.id.et_stock);
        EditText etCodigo = dialogView.findViewById(R.id.et_codigo_barras);

        if (existente != null) {
            etNombre.setText(existente.nombre);
            etCategoria.setText(existente.categoria);
            etPrecio.setText(String.valueOf(existente.precio));
            etStock.setText(String.valueOf(existente.stock));
            etCodigo.setText(existente.codigoBarras);
        }

        new AlertDialog.Builder(requireContext())
                .setTitle(existente == null ? "Nuevo Producto" : "Editar Producto")
                .setView(dialogView)
                .setPositiveButton("Guardar", (d, w) -> {
                    String nombre = etNombre.getText().toString().trim();
                    if (nombre.isEmpty()) { Toast.makeText(requireContext(), "El nombre es requerido", Toast.LENGTH_SHORT).show(); return; }
                    Producto p = existente != null ? existente : new Producto();
                    p.nombre = nombre;
                    p.categoria = etCategoria.getText().toString().trim();
                    try { p.precio = Double.parseDouble(etPrecio.getText().toString()); } catch (Exception e) { p.precio = 0; }
                    try { p.stock = Integer.parseInt(etStock.getText().toString()); } catch (Exception e) { p.stock = 0; }
                    p.codigoBarras = etCodigo.getText().toString().trim();
                    if (existente == null) db.productoDAO().insertar(p);
                    else db.productoDAO().actualizar(p);
                    refrescar();
                })
                .setNegativeButton("Cancelar", null).show();
    }

    private void refrescar() {
        lista.clear();
        lista.addAll(db.productoDAO().obtenerTodos());
        adapter.notifyDataSetChanged();
    }
}
