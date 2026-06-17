package com.example.evaluacion_practica_2;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.evaluacion_practica_2.adapter.ServicioAdapter;
import com.example.evaluacion_practica_2.data.AppDB;
import com.example.evaluacion_practica_2.modelos.Clientes;
import com.example.evaluacion_practica_2.modelos.Servicios;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ServiciosFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ServiciosFragment extends Fragment {

    private RecyclerView recyclerView;
    private ServicioAdapter adapter;
    private TextView tvSinServicios;
    private EditText etBuscarServicio;

    private AppDB db;
    private final ExecutorService databaseExecutor = Executors.newSingleThreadExecutor();
    private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());


    private List<Clientes> listaClientesGlobal = new ArrayList<>();

    public ServiciosFragment() {

    }
    @Override
    public void onResume() {
        super.onResume();
        if(getActivity() != null && ((AppCompatActivity) getActivity()).getSupportActionBar()!=null){
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Servicios");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_servicios, container, false);

        db = AppDB.getInstance(getContext());
        recyclerView = view.findViewById(R.id.rv_servicios);
        tvSinServicios = view.findViewById(R.id.tv_sin_servicios);
        Spinner spBuscarServicio = view.findViewById(R.id.sp_buscar_servicio);
        FloatingActionButton fabAgregar = view.findViewById(R.id.fab_agregar_servicio);

        adapter = new ServicioAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(this::mostrarOpcionesServicio);

        if (fabAgregar != null) {
            fabAgregar.setOnClickListener(v -> abrirDialogoServicio(null));
        }

        if (spBuscarServicio != null) {
            spBuscarServicio.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {

                    String tipoSeleccionado = parent.getItemAtPosition(position).toString();
                    if("Todos".equalsIgnoreCase(tipoSeleccionado)){
                        cargarDatosMaestros();
                    }else {
                        buscarServiciosPorTipoQuery(tipoSeleccionado);
                    }

                }

                @Override
                public void onNothingSelected(android.widget.AdapterView<?> parent) {

                }
            });
        }

        cargarDatosMaestros();
        return view;
    }

    private void cargarDatosMaestros() {
        databaseExecutor.execute(() -> {
            List<Clientes> clientes = db.clientesDAO().obtenerClientes();
            List<Servicios> servicios = db.serviciosDAO().obtenerTodosLoServicios();

            mainThreadHandler.post(() -> {
                listaClientesGlobal = clientes;
                adapter.setClientesMap(clientes);

                actualizarListaEnPantalla(servicios, "No hay servicios registrados.");
            });
        });
    }

    private void buscarServiciosPorTipoQuery(String tipo) {
        databaseExecutor.execute(() -> {
            // Consulta directa por coincidencia exacta en Room
            List<Servicios> listaFiltrada = db.serviciosDAO().obtenerServiciosPorTipo(tipo);

            mainThreadHandler.post(() -> {
                actualizarListaEnPantalla(listaFiltrada, "No se encontraron servicios de tipo: " + tipo);
            });
        });
    }
    private void actualizarListaEnPantalla(List<Servicios> servicios, String mensajeVacio) {
        if (servicios.isEmpty()) {
            tvSinServicios.setText(mensajeVacio);
            tvSinServicios.setVisibility(VISIBLE);
            recyclerView.setVisibility(GONE);
        } else {
            tvSinServicios.setVisibility(GONE);
            recyclerView.setVisibility(VISIBLE);
            adapter.setServicios(servicios);
        }
    }

    private void mostrarOpcionesServicio(Servicios servicio) {
        CharSequence[] opciones = {"Editar Servicio", "Eliminar Servicio"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Acciones de Servicio");
        builder.setItems(opciones, (dialog, item) -> {
            if (item == 0) {
                abrirDialogoServicio(servicio);
            } else if (item == 1) {
                evaluarEliminacionServicio(servicio);
            }
        });
        builder.show();
    }

    private void abrirDialogoServicio(@Nullable Servicios servicioEditar) {
        View viewDialogo = LayoutInflater.from(getContext()).inflate(R.layout.dialog_nuevo_servicio, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(viewDialogo);
        AlertDialog dialog = builder.create();

        TextView tvTitulo = viewDialogo.findViewById(R.id.tv_dialog_servicio_titulo);
        Spinner spCliente = viewDialogo.findViewById(R.id.sp_dialog_servicio_cliente);
        Spinner spTipo = viewDialogo.findViewById(R.id.sp_dialog_servicio_tipo);
        Spinner spEstado = viewDialogo.findViewById(R.id.sp_dialog_servicio_estado);
        EditText etCostoMo = viewDialogo.findViewById(R.id.et_dialog_servicio_costo_mo);
        EditText etCostoMat = viewDialogo.findViewById(R.id.et_dialog_servicio_costo_mat);
        EditText etDescripcion = viewDialogo.findViewById(R.id.et_dialog_servicio_descripcion);
        Button btnCancelar = viewDialogo.findViewById(R.id.btn_dialog_servicio_cancelar);
        Button btnGuardar = viewDialogo.findViewById(R.id.btn_dialog_servicio_guardar);

        List<String> nombresClientes = new ArrayList<>();
        for (Clientes c : listaClientesGlobal) {
            nombresClientes.add(c.getNombresCompleto());
        }
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, nombresClientes);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCliente.setAdapter(spinnerAdapter);

        boolean esEdicion = (servicioEditar != null);
        if (esEdicion) {
            tvTitulo.setText("Editar Servicio");
            etCostoMo.setText(String.valueOf(servicioEditar.getCostoManoObra()));
            etCostoMat.setText(String.valueOf(servicioEditar.getCostoMateriales()));
            etDescripcion.setText(servicioEditar.getDescripcion());

            for (int i = 0; i < listaClientesGlobal.size(); i++) {
                if (listaClientesGlobal.get(i).getId() == servicioEditar.getClienteId()) {
                    spCliente.setSelection(i);
                    break;
                }
            }

            ArrayAdapter<CharSequence> adapterTipo = (ArrayAdapter<CharSequence>) spTipo.getAdapter();
            if (adapterTipo != null)
                spTipo.setSelection(adapterTipo.getPosition(servicioEditar.getTipoServicio()));

            ArrayAdapter<CharSequence> adapterEstado = (ArrayAdapter<CharSequence>) spEstado.getAdapter();
            if (adapterEstado != null)
                spEstado.setSelection(adapterEstado.getPosition(servicioEditar.getEstado()));
        }

        btnCancelar.setOnClickListener(v -> dialog.dismiss());

        btnGuardar.setOnClickListener(v -> {
            if (listaClientesGlobal.isEmpty()) {
                Toast.makeText(getContext(), "Debe registrar un cliente primero", Toast.LENGTH_SHORT).show();
                return;
            }

            String txtCostoMo = etCostoMo.getText().toString().trim();
            String txtCostoMat = etCostoMat.getText().toString().trim();
            String descripcion = etDescripcion.getText().toString().trim();

            if (descripcion.isBlank()) {
                etDescripcion.setError("La descripción es requerida");
                return;
            }

            double costoMo = txtCostoMo.isEmpty() ? 0.0 : Double.parseDouble(txtCostoMo);
            double costoMat = txtCostoMat.isEmpty() ? 0.0 : Double.parseDouble(txtCostoMat);

            // Obtener ID del cliente mapeando la posición del Spinner con nuestra lista estructurada
            int posicionCliente = spCliente.getSelectedItemPosition();
            long idClienteSeleccionado = listaClientesGlobal.get(posicionCliente).getId();

            String tipoSeleccionado = spTipo.getSelectedItem().toString();
            String estadoSeleccionado = spEstado.getSelectedItem().toString();

            // Instanciar o mutar el objeto persistente
            Servicios servicioTarget = esEdicion ? servicioEditar : new Servicios();
            servicioTarget.setClienteId((int) idClienteSeleccionado);
            servicioTarget.setTipoServicio(tipoSeleccionado);
            servicioTarget.setEstado(estadoSeleccionado);
            servicioTarget.setCostoManoObra(costoMo);
            servicioTarget.setCostoMateriales(costoMat);
            servicioTarget.setDescripcion(descripcion);

            databaseExecutor.execute(() -> {
                if (esEdicion) {
                    db.serviciosDAO().actualizar(servicioTarget);
                } else {
                    db.serviciosDAO().crear(servicioTarget);
                }

                mainThreadHandler.post(() -> {
                    Toast.makeText(getContext(), esEdicion ? "Servicio actualizado" : "Servicio registrado", Toast.LENGTH_SHORT).show();
                    cargarDatosMaestros();
                    dialog.dismiss();
                });
            });
        });

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        dialog.show();
    }

    // REGLA DE NEGOCIO OBLIGATORIA: Validación restrictiva del estado del servicio antes del borrado físico
    private void evaluarEliminacionServicio(Servicios servicio) {
        // Validación formal: Si no está "Pendiente", se deniega el acceso a hilos de persistencia
        if (!"Pendiente".equalsIgnoreCase(servicio.getEstado())) {
            new AlertDialog.Builder(getContext())
                    .setTitle("Acción Denegada")
                    .setMessage("No se puede eliminar este servicio porque ya se encuentra " + servicio.getEstado() + ". Solo se permite eliminar servicios en estado Pendiente.")
                    .setPositiveButton("Entendido", (dialog, which) -> dialog.dismiss())
                    .show();
            return;
        }

        // Diálogo de confirmación si pasa el filtro preventivo
        new AlertDialog.Builder(getContext())
                .setTitle("Confirmar Eliminación")
                .setMessage("¿Está seguro de eliminar este servicio pendiente? Esta acción no se puede deshacer.")
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    databaseExecutor.execute(() -> {
                        db.serviciosDAO().eliminar(servicio);
                        mainThreadHandler.post(() -> {
                            Toast.makeText(getContext(), "Servicio eliminado con éxito", Toast.LENGTH_SHORT).show();
                            cargarDatosMaestros();
                        });
                    });
                })
                .show();
    }
}