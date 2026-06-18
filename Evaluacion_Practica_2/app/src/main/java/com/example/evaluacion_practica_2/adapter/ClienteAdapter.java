package com.example.evaluacion_practica_2.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.evaluacion_practica_2.R;
import com.example.evaluacion_practica_2.modelos.Cliente;
import java.util.List;

public class ClienteAdapter extends RecyclerView.Adapter<ClienteAdapter.ViewHolder> {
    private final List<Cliente> lista;
    private final OnLongClickListener listener;

    public interface OnLongClickListener { void onLongClick(Cliente c); }

    public ClienteAdapter(List<Cliente> lista, OnLongClickListener listener) {
        this.lista = lista;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cliente, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Cliente c = lista.get(position);
        holder.tvNombre.setText(c.nombre);
        holder.tvTelefono.setText(c.telefono);
        holder.tvTipo.setText(c.tipo.toUpperCase());
        holder.itemView.setOnLongClickListener(v -> { listener.onLongClick(c); return true; });
    }

    @Override
    public int getItemCount() { return lista.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvTelefono, tvTipo;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tv_cli_nombre);
            tvTelefono = itemView.findViewById(R.id.tv_cli_telefono);
            tvTipo = itemView.findViewById(R.id.tv_cli_tipo);
        }
    }
}
