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
import com.example.evaluacion_practica_2.adapter.PedidoAdapter;
import com.example.evaluacion_practica_2.data.AppDB;
import com.example.evaluacion_practica_2.modelos.Cliente;
import com.example.evaluacion_practica_2.modelos.Pedido;
import com.example.evaluacion_practica_2.modelos.PedidoDetalle;
import com.example.evaluacion_practica_2.modelos.Producto;
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
                .setTitle("Venta #" + p.id)
                .setItems(opciones, (dialog, which) -> {
                    if (which == 0) mostrarDialogoPedido(p);
                    else confirmarEliminar(p);
                }).show();
    }

    private void confirmarEliminar(Pedido p) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Eliminar venta")
                .setMessage("¿Eliminar Venta #" + p.id + "?")
                .setPositiveButton("Sí", (d, w) -> {
                    List<PedidoDetalle> detalles = obtenerDetallesVenta(p);
                    devolverStockSiVentaCompletada(p, detalles);
                    db.pedidoDetalleDAO().eliminarPorPedido(p.id);
                    db.pedidoDAO().eliminar(p);
                    refrescar();
                })
                .setNegativeButton("No", null).show();
    }

    private void mostrarDialogoPedido(Pedido existente) {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_pedido, null);
        EditText etDescripcion = dialogView.findViewById(R.id.et_descripcion_ped);
        EditText etCantidad = dialogView.findViewById(R.id.et_cantidad_ped);
        EditText etTotal = dialogView.findViewById(R.id.et_total_ped);
        TextView tvPrecioStock = dialogView.findViewById(R.id.tv_precio_stock_ped);
        TextView tvDetalleProductos = dialogView.findViewById(R.id.tv_detalle_productos_ped);
        Button btnAgregarProducto = dialogView.findViewById(R.id.btn_agregar_producto_ped);
        Spinner spEstado = dialogView.findViewById(R.id.sp_estado_ped);
        Spinner spMetodoPago = dialogView.findViewById(R.id.sp_metodo_pago);
        Spinner spCliente = dialogView.findViewById(R.id.sp_cliente_ped);
        Spinner spProducto = dialogView.findViewById(R.id.sp_producto_ped);

        List<Cliente> clientes = db.clienteDAO().obtenerPorTipo("cliente");
        List<String> nombresClientes = new ArrayList<>();
        nombresClientes.add("Sin cliente");
        for (Cliente c : clientes) nombresClientes.add(c.nombre);
        ArrayAdapter<String> clienteAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, nombresClientes);
        clienteAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCliente.setAdapter(clienteAdapter);

        List<Producto> productos = db.productoDAO().obtenerTodos();
        List<String> nombresProductos = new ArrayList<>();
        for (Producto producto : productos) {
            nombresProductos.add(producto.nombre + " - $ " + String.format(Locale.getDefault(), "%.2f", producto.precio)
                    + " (Stock: " + stockDisponibleBase(producto, existente) + ")");
        }
        ArrayAdapter<String> productoAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, nombresProductos);
        productoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spProducto.setAdapter(productoAdapter);

        String[] estados = {"completado", "pendiente", "cancelado"};
        ArrayAdapter<String> estadoAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, estados);
        estadoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spEstado.setAdapter(estadoAdapter);

        String[] metodos = {"efectivo", "tarjeta"};
        ArrayAdapter<String> metodoAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, metodos);
        metodoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMetodoPago.setAdapter(metodoAdapter);

        List<ItemVenta> carrito = new ArrayList<>();
        if (existente != null) {
            etTotal.setText("$ " + String.format(Locale.getDefault(), "%.2f", existente.total));
            for (int i = 0; i < clientes.size(); i++) if (clientes.get(i).id == existente.clienteId) { spCliente.setSelection(i + 1); break; }
            for (int i = 0; i < estados.length; i++) if (estados[i].equals(existente.estado)) { spEstado.setSelection(i); break; }
            for (int i = 0; i < metodos.length; i++) if (metodos[i].equals(existente.metodoPago)) { spMetodoPago.setSelection(i); break; }
            for (PedidoDetalle detalle : obtenerDetallesVenta(existente)) carrito.add(new ItemVenta(detalle));
        }
        etCantidad.setText("1");

        Runnable refrescarResumen = () -> refrescarCarrito(carrito, tvDetalleProductos, etTotal);
        Runnable refrescarProducto = () -> refrescarProductoSeleccionado(productos, spProducto, tvPrecioStock, existente, carrito);
        spProducto.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) { refrescarProducto.run(); }
            @Override public void onNothingSelected(AdapterView<?> parent) { refrescarProducto.run(); }
        });
        etCantidad.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { refrescarProducto.run(); }
            @Override public void afterTextChanged(Editable s) {}
        });

        btnAgregarProducto.setOnClickListener(v -> {
            if (productos.isEmpty()) {
                Toast.makeText(requireContext(), "Primero registre productos con stock", Toast.LENGTH_SHORT).show();
                return;
            }
            int cantidad = leerCantidad(etCantidad);
            if (cantidad <= 0) {
                Toast.makeText(requireContext(), "Cantidad inválida", Toast.LENGTH_SHORT).show();
                return;
            }
            Producto producto = productos.get(spProducto.getSelectedItemPosition());
            int disponible = stockDisponibleBase(producto, existente) - cantidadEnCarrito(carrito, producto.id);
            if (cantidad > disponible) {
                Toast.makeText(requireContext(), "Stock insuficiente para agregar ese producto", Toast.LENGTH_SHORT).show();
                return;
            }
            agregarAlCarrito(carrito, producto, cantidad);
            etCantidad.setText("1");
            refrescarResumen.run();
            refrescarProducto.run();
        });

        refrescarResumen.run();
        refrescarProducto.run();

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle(existente == null ? "Nueva Venta" : "Editar Venta")
                .setView(dialogView)
                .setPositiveButton("Guardar", null)
                .setNeutralButton("Quitar último", null)
                .setNegativeButton("Cancelar", null).create();

        dialog.setOnShowListener(d -> {
            dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(v -> {
                if (!carrito.isEmpty()) {
                    carrito.remove(carrito.size() - 1);
                    refrescarResumen.run();
                    refrescarProducto.run();
                }
            });

            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                if (carrito.isEmpty()) {
                    Toast.makeText(requireContext(), "Agregue al menos un producto", Toast.LENGTH_SHORT).show();
                    return;
                }

                String estado = spEstado.getSelectedItem().toString();
                if ("completado".equals(estado) && !hayStockParaCarrito(carrito, existente)) {
                    Toast.makeText(requireContext(), "Hay productos sin stock suficiente", Toast.LENGTH_SHORT).show();
                    return;
                }

                Pedido anterior = existente != null ? copiarPedido(existente) : null;
                List<PedidoDetalle> detallesAnteriores = existente != null ? obtenerDetallesVenta(existente) : new ArrayList<>();
                Pedido p = existente != null ? existente : new Pedido();
                ItemVenta primerItem = carrito.get(0);
                p.productoId = primerItem.productoId;
                p.cantidad = primerItem.cantidad;
                p.precioUnitario = primerItem.precioUnitario;
                p.total = totalCarrito(carrito);
                p.estado = estado;
                p.metodoPago = spMetodoPago.getSelectedItem().toString();
                p.fecha = existente != null ? existente.fecha : new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                int posCliente = spCliente.getSelectedItemPosition();
                p.clienteId = (posCliente > 0) ? clientes.get(posCliente - 1).id : 0;
                p.descripcion = descripcionVenta(carrito, etDescripcion.getText().toString().trim());

                if (existente == null) {
                    p.id = (int) db.pedidoDAO().insertar(p);
                } else {
                    db.pedidoDAO().actualizar(p);
                    db.pedidoDetalleDAO().eliminarPorPedido(p.id);
                }

                List<PedidoDetalle> detallesNuevos = guardarDetalles(p.id, carrito);
                aplicarCambioStock(anterior, detallesAnteriores, p, detallesNuevos);
                refrescar();
                dialog.dismiss();
            });
        });
        dialog.show();
    }

    private void refrescar() {
        lista.clear();
        lista.addAll(db.pedidoDAO().obtenerTodos());
        adapter.notifyDataSetChanged();
    }

    private List<PedidoDetalle> obtenerDetallesVenta(Pedido pedido) {
        List<PedidoDetalle> detalles = db.pedidoDetalleDAO().obtenerPorPedido(pedido.id);
        if (detalles.isEmpty() && pedido.productoId > 0 && pedido.cantidad > 0) {
            Producto producto = db.productoDAO().obtenerPorId(pedido.productoId);
            PedidoDetalle detalle = new PedidoDetalle();
            detalle.pedidoId = pedido.id;
            detalle.productoId = pedido.productoId;
            detalle.productoNombre = producto != null ? producto.nombre : pedido.descripcion;
            detalle.cantidad = pedido.cantidad;
            detalle.precioUnitario = pedido.precioUnitario;
            detalle.subtotal = pedido.total;
            detalles.add(detalle);
        }
        return detalles;
    }

    private int stockDisponibleBase(Producto producto, Pedido existente) {
        if (producto == null) return 0;
        int disponible = producto.stock;
        if (existente != null && "completado".equals(existente.estado)) {
            for (PedidoDetalle detalle : obtenerDetallesVenta(existente)) {
                if (detalle.productoId == producto.id) disponible += detalle.cantidad;
            }
        }
        return disponible;
    }

    private void refrescarProductoSeleccionado(List<Producto> productos, Spinner spProducto, TextView tvPrecioStock,
                                               Pedido existente, List<ItemVenta> carrito) {
        if (productos.isEmpty()) {
            tvPrecioStock.setText("No hay productos registrados");
            return;
        }
        Producto producto = productos.get(spProducto.getSelectedItemPosition());
        int disponible = stockDisponibleBase(producto, existente) - cantidadEnCarrito(carrito, producto.id);
        tvPrecioStock.setText("$ " + String.format(Locale.getDefault(), "%.2f", producto.precio)
                + " · Stock disponible: " + Math.max(disponible, 0));
    }

    private void agregarAlCarrito(List<ItemVenta> carrito, Producto producto, int cantidad) {
        for (ItemVenta item : carrito) {
            if (item.productoId == producto.id) {
                item.cantidad += cantidad;
                item.subtotal = item.cantidad * item.precioUnitario;
                return;
            }
        }
        carrito.add(new ItemVenta(producto, cantidad));
    }

    private void refrescarCarrito(List<ItemVenta> carrito, TextView tvDetalleProductos, EditText etTotal) {
        if (carrito.isEmpty()) {
            tvDetalleProductos.setText("Sin productos agregados");
            etTotal.setText("$ 0.00");
            return;
        }
        StringBuilder resumen = new StringBuilder();
        for (ItemVenta item : carrito) {
            resumen.append(item.productoNombre)
                    .append(" x").append(item.cantidad)
                    .append(" = $ ")
                    .append(String.format(Locale.getDefault(), "%.2f", item.subtotal))
                    .append("\n");
        }
        tvDetalleProductos.setText(resumen.toString().trim());
        etTotal.setText("$ " + String.format(Locale.getDefault(), "%.2f", totalCarrito(carrito)));
    }

    private boolean hayStockParaCarrito(List<ItemVenta> carrito, Pedido existente) {
        Map<Integer, Integer> cantidades = new HashMap<>();
        for (ItemVenta item : carrito) cantidades.put(item.productoId, cantidades.getOrDefault(item.productoId, 0) + item.cantidad);
        for (Map.Entry<Integer, Integer> entry : cantidades.entrySet()) {
            Producto producto = db.productoDAO().obtenerPorId(entry.getKey());
            if (producto == null || entry.getValue() > stockDisponibleBase(producto, existente)) return false;
        }
        return true;
    }

    private int cantidadEnCarrito(List<ItemVenta> carrito, int productoId) {
        int cantidad = 0;
        for (ItemVenta item : carrito) if (item.productoId == productoId) cantidad += item.cantidad;
        return cantidad;
    }

    private int leerCantidad(EditText etCantidad) {
        try { return Integer.parseInt(etCantidad.getText().toString()); } catch (Exception e) { return 0; }
    }

    private double totalCarrito(List<ItemVenta> carrito) {
        double total = 0;
        for (ItemVenta item : carrito) total += item.subtotal;
        return total;
    }

    private String descripcionVenta(List<ItemVenta> carrito, String nota) {
        StringBuilder descripcion = new StringBuilder();
        for (int i = 0; i < carrito.size(); i++) {
            ItemVenta item = carrito.get(i);
            if (i > 0) descripcion.append(", ");
            descripcion.append(item.productoNombre).append(" x").append(item.cantidad);
        }
        if (!nota.isEmpty()) descripcion.append(" - ").append(nota);
        return descripcion.toString();
    }

    private List<PedidoDetalle> guardarDetalles(int pedidoId, List<ItemVenta> carrito) {
        List<PedidoDetalle> detalles = new ArrayList<>();
        for (ItemVenta item : carrito) {
            PedidoDetalle detalle = new PedidoDetalle();
            detalle.pedidoId = pedidoId;
            detalle.productoId = item.productoId;
            detalle.productoNombre = item.productoNombre;
            detalle.cantidad = item.cantidad;
            detalle.precioUnitario = item.precioUnitario;
            detalle.subtotal = item.subtotal;
            db.pedidoDetalleDAO().insertar(detalle);
            detalles.add(detalle);
        }
        return detalles;
    }

    private Pedido copiarPedido(Pedido original) {
        Pedido copia = new Pedido();
        copia.id = original.id;
        copia.clienteId = original.clienteId;
        copia.productoId = original.productoId;
        copia.cantidad = original.cantidad;
        copia.precioUnitario = original.precioUnitario;
        copia.descripcion = original.descripcion;
        copia.total = original.total;
        copia.estado = original.estado;
        copia.fecha = original.fecha;
        copia.metodoPago = original.metodoPago;
        return copia;
    }

    private void aplicarCambioStock(Pedido anterior, List<PedidoDetalle> detallesAnteriores,
                                    Pedido actual, List<PedidoDetalle> detallesNuevos) {
        devolverStockSiVentaCompletada(anterior, detallesAnteriores);
        if (actual != null && "completado".equals(actual.estado)) {
            for (PedidoDetalle detalle : detallesNuevos) {
                db.productoDAO().ajustarStock(detalle.productoId, -detalle.cantidad);
            }
        }
    }

    private void devolverStockSiVentaCompletada(Pedido pedido, List<PedidoDetalle> detalles) {
        if (pedido != null && "completado".equals(pedido.estado)) {
            for (PedidoDetalle detalle : detalles) {
                db.productoDAO().ajustarStock(detalle.productoId, detalle.cantidad);
            }
        }
    }

    private static class ItemVenta {
        int productoId;
        String productoNombre;
        int cantidad;
        double precioUnitario;
        double subtotal;

        ItemVenta(Producto producto, int cantidad) {
            this.productoId = producto.id;
            this.productoNombre = producto.nombre;
            this.cantidad = cantidad;
            this.precioUnitario = producto.precio;
            this.subtotal = producto.precio * cantidad;
        }

        ItemVenta(PedidoDetalle detalle) {
            this.productoId = detalle.productoId;
            this.productoNombre = detalle.productoNombre;
            this.cantidad = detalle.cantidad;
            this.precioUnitario = detalle.precioUnitario;
            this.subtotal = detalle.subtotal;
        }
    }
}
