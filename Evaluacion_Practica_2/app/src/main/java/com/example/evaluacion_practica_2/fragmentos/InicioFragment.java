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
        TextView tvPendientes = view.findViewById(R.id.tv_pedidos_pendientes);
        TextView tvVentas = view.findViewById(R.id.tv_ventas_hoy);
        TextView tvStockBajo = view.findViewById(R.id.tv_stock_bajo);

        SessionManager session = new SessionManager(requireContext());
        tvBienvenida.setText("Bienvenido, " + session.getNombre());

        String hoy = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        tvProductos.setText(String.valueOf(db.productoDAO().contarProductos()));
        tvPendientes.setText(String.valueOf(db.pedidoDAO().contarPendientes()));
        tvVentas.setText("$ " + String.format(Locale.getDefault(), "%.2f", db.pedidoDAO().totalVentasDia(hoy)));
        tvStockBajo.setText(String.valueOf(db.productoDAO().stockBajo().size()));

        return view;
    }
}
