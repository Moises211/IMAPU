package com.example.evaluacion_practica_2.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.evaluacion_practica_2.R;
import com.example.evaluacion_practica_2.modelos.Pedido;
import java.util.List;
import java.util.Locale;

public class PedidoAdapter extends RecyclerView.Adapter<PedidoAdapter.ViewHolder> {
    private final List<Pedido> lista;
    private final OnLongClickListener listener;

    public interface OnLongClickListener { void onLongClick(Pedido p); }

    public PedidoAdapter(List<Pedido> lista, OnLongClickListener listener) {
        this.lista = lista;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pedido, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Pedido p = lista.get(position);
        holder.tvId.setText("Pedido #" + p.id);
        holder.tvDescripcion.setText(p.descripcion);
        holder.tvTotal.setText("$ " + String.format(Locale.getDefault(), "%.2f", p.total));
        holder.tvEstado.setText(p.estado);
        holder.tvFecha.setText(p.fecha);
        holder.itemView.setOnLongClickListener(v -> { listener.onLongClick(p); return true; });
    }

    @Override
    public int getItemCount() { return lista.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvId, tvDescripcion, tvTotal, tvEstado, tvFecha;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvId = itemView.findViewById(R.id.tv_ped_id);
            tvDescripcion = itemView.findViewById(R.id.tv_ped_descripcion);
            tvTotal = itemView.findViewById(R.id.tv_ped_total);
            tvEstado = itemView.findViewById(R.id.tv_ped_estado);
            tvFecha = itemView.findViewById(R.id.tv_ped_fecha);
        }
    }
}
