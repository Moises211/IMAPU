package com.example.evaluacion_practica_2.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.evaluacion_practica_2.R;
import com.example.evaluacion_practica_2.modelos.Usuario;
import java.util.List;

public class UsuarioAdapter extends RecyclerView.Adapter<UsuarioAdapter.ViewHolder> {
    private final List<Usuario> lista;
    private final OnLongClickListener listener;

    public interface OnLongClickListener { void onLongClick(Usuario u); }

    public UsuarioAdapter(List<Usuario> lista, OnLongClickListener listener) {
        this.lista = lista;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_usuario, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Usuario u = lista.get(position);
        holder.tvNombre.setText(u.nombre);
        holder.tvUsuario.setText("Usuario: " + u.usuario);
        holder.tvRol.setText(u.rol.toUpperCase());
        holder.itemView.setOnLongClickListener(v -> {
            listener.onLongClick(u);
            return true;
        });
    }

    @Override
    public int getItemCount() { return lista.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvUsuario, tvRol;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tv_usuario_nombre);
            tvUsuario = itemView.findViewById(R.id.tv_usuario_user);
            tvRol = itemView.findViewById(R.id.tv_usuario_rol);
        }
    }
}
