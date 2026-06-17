package com.example.evaluacion_practica_2.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.evaluacion_practica_2.R;
import com.example.evaluacion_practica_2.modelos.Clientes;
import com.example.evaluacion_practica_2.modelos.Servicios;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServicioAdapter extends RecyclerView.Adapter<ServicioAdapter.ServicioViewHolder>{
    private List<Servicios> listaServicios;
    private Map<Long, String> mapaClientes = new HashMap<>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Servicios servicio);
    }

    public ServicioAdapter(List<Servicios> listaServicios) {
        this.listaServicios = listaServicios;
    }

    // Método para actualizar la lista de servicios
    public void setServicios(List<Servicios> servicios) {
        this.listaServicios = servicios;
        notifyDataSetChanged();
    }

    // Método indispensable para pasarle los clientes desde el Fragment y poder traducir los IDs a nombres legibles
    public void setClientesMap(List<Clientes> clientes) {
        this.mapaClientes.clear();
        if (clientes != null) {
            for (Clientes c : clientes) {

                mapaClientes.put((long) c.getId(), c.getNombresCompleto());
            }
        }
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ServicioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_servicio, parent, false);
        return new ServicioViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ServicioViewHolder holder, int position) {
        Servicios servicio = listaServicios.get(position);


        String nombreCliente = mapaClientes.get((long) servicio.getClienteId());
        if (nombreCliente != null) {
            holder.tvCliente.setText("Cliente: " + nombreCliente);
        } else {
            holder.tvCliente.setText("Cliente: No asignado o eliminado");
        }


        holder.tvTipo.setText("Tipo: " + servicio.getTipoServicio());
        holder.tvEstado.setText("Estado: " + servicio.getEstado());
        holder.tvCostos.setText("Costos: M.O: $" + servicio.getCostoManoObra() + " | Mat: $" + servicio.getCostoMateriales());
        holder.tvDescripcion.setText(servicio.getDescripcion());

        if ("Pendiente".equalsIgnoreCase(servicio.getEstado())) {
            holder.tvEstado.setTextColor(android.graphics.Color.parseColor("#8B949E"));
        } else if ("En Proceso".equalsIgnoreCase(servicio.getEstado())) {
            holder.tvEstado.setTextColor(android.graphics.Color.parseColor("#FFC107"));
        } else if ("Completado".equalsIgnoreCase(servicio.getEstado())) {
            holder.tvEstado.setTextColor(android.graphics.Color.parseColor("#27AE60"));
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(servicio);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaServicios != null ? listaServicios.size() : 0;
    }

    static class ServicioViewHolder extends RecyclerView.ViewHolder {
        TextView tvCliente, tvTipo, tvEstado, tvCostos, tvDescripcion;

        public ServicioViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCliente = itemView.findViewById(R.id.tv_item_servicio_cliente);
            tvTipo = itemView.findViewById(R.id.tv_item_servicio_tipo);
            tvEstado = itemView.findViewById(R.id.tv_item_servicio_estado);
            tvCostos = itemView.findViewById(R.id.tv_item_servicio_costos);
            tvDescripcion = itemView.findViewById(R.id.tv_item_servicio_descripcion);
        }
    }
}
