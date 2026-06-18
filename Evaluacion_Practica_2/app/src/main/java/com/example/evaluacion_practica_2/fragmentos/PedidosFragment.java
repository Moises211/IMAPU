package com.example.evaluacion_practica_2.fragmentos;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.evaluacion_practica_2.R;
import com.example.evaluacion_practica_2.adapter.PedidoAdapter;
import com.example.evaluacion_practica_2.data.AppDB;
import com.example.evaluacion_practica_2.modelos.Cliente;
import com.example.evaluacion_practica_2.modelos.Pedido;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.text.SimpleDateFormat;
import java.util.*;

public class PedidosFragment extends Fragment {
    private AppDB db;
    private PedidoAdapter adapter;
    private List<Pedido> lista;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pedidos, container, false);
        db = AppDB.getInstance(requireContext());

        RecyclerView rv = view.findViewById(R.id.rv_pedidos);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        lista = db.pedidoDAO().obtenerTodos();
        adapter = new PedidoAdapter(lista, this::onPedidoLongClick);
        rv.setAdapter(adapter);

        FloatingActionButton fab = view.findViewById(R.id.fab_nuevo_pedido);
        fab.setOnClickListener(v -> mostrarDialogoPedido(null));

        return view;
    }

    private void onPedidoLongClick(Pedido p) {
        String[] opciones = {"Editar", "Eliminar"};
        new AlertDialog.Builder(requireContext())
                .setTitle("Pedido #" + p.id)
                .setItems(opciones, (dialog, which) -> {
                    if (which == 0) mostrarDialogoPedido(p);
                    else confirmarEliminar(p);
                }).show();
    }

    private void confirmarEliminar(Pedido p) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Eliminar pedido")
                .setMessage("¿Eliminar Pedido #" + p.id + "?")
                .setPositiveButton("Sí", (d, w) -> { db.pedidoDAO().eliminar(p); refrescar(); })
                .setNegativeButton("No", null).show();
    }

    private void mostrarDialogoPedido(Pedido existente) {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_pedido, null);
        EditText etDescripcion = dialogView.findViewById(R.id.et_descripcion_ped);
        EditText etTotal = dialogView.findViewById(R.id.et_total_ped);
        Spinner spEstado = dialogView.findViewById(R.id.sp_estado_ped);
        Spinner spMetodoPago = dialogView.findViewById(R.id.sp_metodo_pago);
        Spinner spCliente = dialogView.findViewById(R.id.sp_cliente_ped);

        List<Cliente> clientes = db.clienteDAO().obtenerPorTipo("cliente");
        List<String> nombresClientes = new ArrayList<>();
        nombresClientes.add("Sin cliente");
        for (Cliente c : clientes) nombresClientes.add(c.nombre);
        ArrayAdapter<String> clienteAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, nombresClientes);
        clienteAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCliente.setAdapter(clienteAdapter);

        String[] estados = {"pendiente", "completado", "cancelado"};
        ArrayAdapter<String> estadoAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, estados);
        estadoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spEstado.setAdapter(estadoAdapter);

        String[] metodos = {"efectivo", "tarjeta"};
        ArrayAdapter<String> metodoAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, metodos);
        metodoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMetodoPago.setAdapter(metodoAdapter);

        if (existente != null) {
            etDescripcion.setText(existente.descripcion);
            etTotal.setText(String.valueOf(existente.total));
            for (int i = 0; i < estados.length; i++) if (estados[i].equals(existente.estado)) { spEstado.setSelection(i); break; }
            for (int i = 0; i < metodos.length; i++) if (metodos[i].equals(existente.metodoPago)) { spMetodoPago.setSelection(i); break; }
        }

        new AlertDialog.Builder(requireContext())
                .setTitle(existente == null ? "Nuevo Pedido" : "Editar Pedido")
                .setView(dialogView)
                .setPositiveButton("Guardar", (d, w) -> {
                    Pedido p = existente != null ? existente : new Pedido();
                    p.descripcion = etDescripcion.getText().toString().trim();
                    try { p.total = Double.parseDouble(etTotal.getText().toString()); } catch (Exception e) { p.total = 0; }
                    p.estado = spEstado.getSelectedItem().toString();
                    p.metodoPago = spMetodoPago.getSelectedItem().toString();
                    p.fecha = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                    int posCliente = spCliente.getSelectedItemPosition();
                    p.clienteId = (posCliente > 0) ? clientes.get(posCliente - 1).id : 0;
                    if (existente == null) db.pedidoDAO().insertar(p);
                    else db.pedidoDAO().actualizar(p);
                    refrescar();
                })
                .setNegativeButton("Cancelar", null).show();
    }

    private void refrescar() {
        lista.clear();
        lista.addAll(db.pedidoDAO().obtenerTodos());
        adapter.notifyDataSetChanged();
    }
}
