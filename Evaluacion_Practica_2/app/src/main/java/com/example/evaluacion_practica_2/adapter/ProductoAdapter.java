package com.example.evaluacion_practica_2.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.evaluacion_practica_2.R;
import com.example.evaluacion_practica_2.modelos.Producto;
import java.util.List;
import java.util.Locale;

public class ProductoAdapter extends RecyclerView.Adapter<ProductoAdapter.ViewHolder> {
    private final List<Producto> lista;
    private final OnLongClickListener listener;

    public interface OnLongClickListener { void onLongClick(Producto p); }

    public ProductoAdapter(List<Producto> lista, OnLongClickListener listener) {
        this.lista = lista;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_producto, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Producto p = lista.get(position);
        holder.tvNombre.setText(p.nombre);
        holder.tvCategoria.setText(p.categoria);
        holder.tvPrecio.setText("$ " + String.format(Locale.getDefault(), "%.2f", p.precio));
        holder.tvStock.setText("Stock: " + p.stock);
        if (p.stock <= 5) holder.tvStock.setTextColor(0xFFE53935);
        else holder.tvStock.setTextColor(0xFF388E3C);
        holder.itemView.setOnLongClickListener(v -> { listener.onLongClick(p); return true; });
    }

    @Override
    public int getItemCount() { return lista.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvCategoria, tvPrecio, tvStock;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tv_prod_nombre);
            tvCategoria = itemView.findViewById(R.id.tv_prod_categoria);
            tvPrecio = itemView.findViewById(R.id.tv_prod_precio);
            tvStock = itemView.findViewById(R.id.tv_prod_stock);
        }
    }
}
