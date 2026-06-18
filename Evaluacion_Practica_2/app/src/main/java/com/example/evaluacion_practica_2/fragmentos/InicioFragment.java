package com.example.evaluacion_practica_2.fragmentos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.evaluacion_practica_2.R;
import com.example.evaluacion_practica_2.SessionManager;
import com.example.evaluacion_practica_2.data.AppDB;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class InicioFragment extends Fragment {
    private AppDB db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inicio, container, false);
        db = AppDB.getInstance(requireContext());

        TextView tvBienvenida = view.findViewById(R.id.tv_bienvenida);
        TextView tvProductos = view.findViewById(R.id.tv_total_productos);
        TextView tvClientes = view.findViewById(R.id.tv_total_clientes);
        TextView tvProveedores = view.findViewById(R.id.tv_total_proveedores);
        TextView tvPendientes = view.findViewById(R.id.tv_pedidos_pendientes);
        TextView tvVentas = view.findViewById(R.id.tv_ventas_hoy);
        TextView tvVentasCantidad = view.findViewById(R.id.tv_ventas_cantidad);
        TextView tvStockBajo = view.findViewById(R.id.tv_stock_bajo);
        TextView tvStockTotal = view.findViewById(R.id.tv_stock_total);

        SessionManager session = new SessionManager(requireContext());
        tvBienvenida.setText("Bienvenido, " + session.getNombre());

        String hoy = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        tvProductos.setText(String.valueOf(db.productoDAO().contarProductos()));
        tvClientes.setText(String.valueOf(db.clienteDAO().contarPorTipo("cliente")));
        tvProveedores.setText(String.valueOf(db.clienteDAO().contarPorTipo("proveedor")));
        tvPendientes.setText(String.valueOf(db.pedidoDAO().contarPendientes()));
        tvVentas.setText("$ " + String.format(Locale.getDefault(), "%.2f", db.pedidoDAO().totalVentasDia(hoy)));
        tvVentasCantidad.setText(db.pedidoDAO().contarVentasDia(hoy) + " ventas completadas");
        tvStockBajo.setText(String.valueOf(db.productoDAO().stockBajo().size()));
        tvStockTotal.setText(db.productoDAO().totalStock() + " unidades");

        return view;
    }
}
