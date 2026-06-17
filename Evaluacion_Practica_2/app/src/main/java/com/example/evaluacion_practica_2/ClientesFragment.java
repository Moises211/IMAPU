package com.example.evaluacion_practica_2;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.evaluacion_practica_2.adapter.ClienteAdapter;
import com.example.evaluacion_practica_2.data.AppDB;
import com.example.evaluacion_practica_2.modelos.Clientes;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ClientesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ClientesFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RecyclerView recyclerView;
    private ClienteAdapter adapter;
    private TextView tvSinClientes;

    private AppDB db;
    private final ExecutorService databaseExecutor = Executors.newSingleThreadExecutor();
    private final Handler mainTreadHandler = new Handler(Looper.getMainLooper());

    public ClientesFragment() {

    }
    @Override
    public void onResume() {
        super.onResume();
        if(getActivity() != null && ((AppCompatActivity) getActivity()).getSupportActionBar()!=null){
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Clientes");
        }
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ClientesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ClientesFragment newInstance(String param1, String param2) {
        ClientesFragment fragment = new ClientesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_clientes, container, false);
        db = AppDB.getInstance(getContext());
        recyclerView = view.findViewById(R.id.rv_clientes);
        tvSinClientes = view.findViewById(R.id.tv_sin_clientes);
        FloatingActionButton fabAgegar = view.findViewById(R.id.fab_agregar_cliente);
        EditText etBuscarCliente = view.findViewById(R.id.et_buscar_cliente);

        adapter = new ClienteAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(cliente -> {
            mostrarDialogoOpciones(cliente);
        });
        Clientes clientes = new Clientes();
        if (fabAgegar != null) {
            fabAgegar.setOnClickListener(v -> abrirDialogoNuevoCliente(clientes));
        }

        if (etBuscarCliente != null){
            etBuscarCliente.addTextChangedListener(new android.text.TextWatcher(){

                @Override
                public void afterTextChanged(Editable s) {

                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    buscarClientesQuery(s.toString().trim());
                }
            });
        }

        cargarClientes();
        return view;
    }

    private void cargarClientes() {
        databaseExecutor.execute(() -> {
            List<Clientes> clientes = db.clientesDAO().obtenerClientes();

            mainTreadHandler.post(() -> {
                if (clientes.isEmpty()) {
                    tvSinClientes.setText("No hay clientes registrados.");
                    tvSinClientes.setVisibility(VISIBLE);
                    recyclerView.setVisibility(GONE);
                } else {
                    tvSinClientes.setVisibility(GONE);
                    recyclerView.setVisibility(VISIBLE);
                    adapter.setClientes(clientes);
                    adapter.notifyDataSetChanged();
                }
            });
        });
    }


    private void abrirDialogoNuevoCliente(Clientes clientes) {
        View viewDialogo = LayoutInflater.from(getContext()).inflate(R.layout.dialog_nuevo_cliente, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(viewDialogo);
        AlertDialog dialog = builder.create();

        TextView tvTitulo = viewDialogo.findViewById(R.id.tv_dialog_titulo);
        EditText etNombre = viewDialogo.findViewById(R.id.et_dialog_nombre);
        EditText etTelefono = viewDialogo.findViewById(R.id.et_dialog_telefono);
        EditText etEmail = viewDialogo.findViewById(R.id.et_dialog_email);
        EditText etDireccion = viewDialogo.findViewById(R.id.et_dialog_direccion);
        EditText etMunicipio = viewDialogo.findViewById(R.id.et_dialog_municipio);
        EditText etNotas = viewDialogo.findViewById(R.id.et_dialog_notas);
        Button btnCancelar = viewDialogo.findViewById(R.id.btn_dialog_cancelar);
        Button btnGuardar = viewDialogo.findViewById(R.id.btn_dialog_guardar);

        String msg;

        if (!TextUtils.isEmpty(clientes.getNombresCompleto())) {
            tvTitulo.setText("Editar Cliente");
            etNombre.setText(clientes.getNombresCompleto());
            etTelefono.setText(clientes.getTelefono());
            etEmail.setText(clientes.getEmail());
            etDireccion.setText(clientes.getDireccion());
            etMunicipio.setText(clientes.getMunicipio());
            etNotas.setText(clientes.getNotas());

            msg = "Cambios guardados con exito";
        } else {
            msg = "Cliente registrado con exito";
        }

        btnCancelar.setOnClickListener(v -> dialog.dismiss());

        btnGuardar.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();
            String telefono = etTelefono.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String direccion = etDireccion.getText().toString().trim();
            String municipio = etMunicipio.getText().toString().trim();
            String notas = etNotas.getText().toString().trim();

            if (nombre.isBlank()) {
                etNombre.setError("El nombre es requerido");
                return;
            }

            if (telefono.isBlank()) {
                etTelefono.setError("El telefono es requerido");
                return;
            } else if (!Patterns.PHONE.matcher(telefono).matches() || telefono.length() > 8) {
                etTelefono.setError("El formato del telefono es incorrecto");
                return;
            }

            if (email.isBlank()) {
                etEmail.setError("El email es requerido");
                return;
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etTelefono.setError("El formato del email es incorrecto");
                return;
            }
            Clientes nuevoCliente = new Clientes();

            if (TextUtils.isEmpty(clientes.getNombresCompleto())) {
                nuevoCliente.setNombresCompleto(nombre);
                nuevoCliente.setTelefono(telefono);
                nuevoCliente.setEmail(email);
                nuevoCliente.setDireccion(direccion);
                nuevoCliente.setMunicipio(municipio);
                nuevoCliente.setNotas(notas);
            } else {
                clientes.setNombresCompleto(nombre);
                clientes.setTelefono(telefono);
                clientes.setEmail(email);
                clientes.setDireccion(direccion);
                clientes.setMunicipio(municipio);
                clientes.setNotas(notas);
            }

            databaseExecutor.execute(() -> {
                if (!TextUtils.isEmpty(clientes.getNombresCompleto())) {
                    db.clientesDAO().actualizar(clientes);
                } else {
                    db.clientesDAO().crear(nuevoCliente);
                }
                mainTreadHandler.post(() -> {
                    Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                    cargarClientes();
                    dialog.dismiss();
                });
            });

        });

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        dialog.show();
    }

    private void mostrarDialogoOpciones(Clientes clientes) {
        CharSequence[] opciones = {"Editar", "Eliminar", "Ver Detalle"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setItems(opciones, ((dialog, which) -> {
            if (which == 0) {
                abrirDialogoNuevoCliente(clientes);
            } else if (which == 1) {
                mostrarDialogoConfirmarEliminar(clientes);
            } else if (which == 2) {
                if (getActivity() != null){
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, DetalleClienteFragment.newInstance(clientes))
                            .addToBackStack(null)
                            .commit();
                }
            }
        }));
        builder.show();
    }

    private void mostrarDialogoConfirmarEliminar(Clientes clientes) {
        new AlertDialog.Builder(getContext())
                .setTitle("Eliminar Cliente")
                .setMessage("Seguro que desea eliminar de forma permantente a: \n" + clientes.getNombresCompleto()
                        + "\n Esta accion tambien eliminara los servicios vinculados")
                .setNegativeButton("Cancelar", ((dialog, which) -> dialog.dismiss()))
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    databaseExecutor.execute(() -> {
                        db.clientesDAO().eliminar(clientes);
                        mainTreadHandler.post(() -> {
                            Toast.makeText(getContext(), "Registro Eliminado", Toast.LENGTH_SHORT);
                            cargarClientes();
                        });
                    });
                }).show();
    }

    private void buscarClientesQuery(String textoBusqueda) {
        if (textoBusqueda.isEmpty()) {
            cargarClientes();
            return;
        }

        databaseExecutor.execute(() -> {
            String queryFormateado = "%" + textoBusqueda + "%";
            List<Clientes> listaFiltrada = db.clientesDAO().buscarPorNombre(queryFormateado);

            mainTreadHandler.post(() ->{
               if (listaFiltrada.isEmpty()){
                   tvSinClientes.setText("No se encontraron clientes con ese nombre");
                   tvSinClientes.setVisibility(VISIBLE);
                   recyclerView.setVisibility(GONE);
               }else {
                   tvSinClientes.setVisibility(GONE);
                   recyclerView.setVisibility(VISIBLE);
                   adapter.setClientes(listaFiltrada);
               }
            });
        });
    }
}