package com.example.evaluacion_practica_2;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.evaluacion_practica_2.data.AppDB;

import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InicioFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InicioFragment extends Fragment {

    private TextView tvClientesCount, tvPendientesCount, tvIngresosTotal;
    private AppDB db;
    private final ExecutorService databaseExecutor = Executors.newSingleThreadExecutor();
    private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());

    public InicioFragment() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inicio, container, false);

        db = AppDB.getInstance(getContext());


        tvClientesCount = view.findViewById(R.id.tv_dash_clientes_count);
        tvPendientesCount = view.findViewById(R.id.tv_dash_pendientes_count);
        tvIngresosTotal = view.findViewById(R.id.tv_dash_ingresos_total);


        Button btnIrClientes = view.findViewById(R.id.btn_dash_ir_clientes);
        Button btnIrServicios = view.findViewById(R.id.btn_dash_ir_servicios);


        if (btnIrClientes != null) {

            btnIrClientes.setOnClickListener(v -> irAFragmento(R.id.nav_clientes));
        }

        if (btnIrServicios != null) {

            btnIrServicios.setOnClickListener(v -> irAFragmento(R.id.nav_servicios));
        }


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (getActivity() != null && ((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Dashboard");
        }

        calcularMetricasDashboard();
    }

    private void calcularMetricasDashboard() {
        databaseExecutor.execute(() -> {

            int totalClientes = db.clientesDAO().contarTotalClientes();
            int totalPendientes = db.serviciosDAO().contarServiciosPendientes();
            double sumaIngresos = db.serviciosDAO().calcularIngresosCompletados();

            mainThreadHandler.post(() -> {

                tvClientesCount.setText(String.valueOf(totalClientes));
                tvPendientesCount.setText(String.valueOf(totalPendientes));


                tvIngresosTotal.setText(String.format(Locale.US, "$%.2f", sumaIngresos));
            });
        });
    }


    private void irAFragmento(int idItemMenu) {
        if (getActivity() != null) {

            com.google.android.material.bottomnavigation.BottomNavigationView bottomNav =
                    getActivity().findViewById(R.id.bottom_navigation);

            if (bottomNav != null) {

                bottomNav.setSelectedItemId(idItemMenu);
            }
        }
    }
}