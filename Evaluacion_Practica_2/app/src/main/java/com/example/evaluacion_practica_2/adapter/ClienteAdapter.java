package com.example.evaluacion_practica_2.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.evaluacion_practica_2.R;
import com.example.evaluacion_practica_2.modelos.Clientes;

import java.util.List;

public class ClienteAdapter extends RecyclerView.Adapter<ClienteAdapter.ClienteViewHolder> {
    private List<Clientes> listaClientes;
    private OnItemClickListener listener;

    public ClienteAdapter(List<Clientes> listaClientes){
        this.listaClientes = listaClientes;
    }

    public void setClientes(List<Clientes> clientes){
        this.listaClientes = clientes;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener{
        void onItemClick(Clientes cliente);
    }

    public  void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    @NonNull
    @Override
    public ClienteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cliente, parent, false);
        ClienteViewHolder clientVH = new ClienteViewHolder(vista);
        return clientVH;
    }


    @Override
    public void onBindViewHolder(@NonNull ClienteViewHolder holder, int position) {
        Clientes cliente = listaClientes.get(position);
        holder.tvNombre.setText(cliente.getNombresCompleto());
        holder.tvTelefono.setText(cliente.getTelefono());
        holder.tvEmail.setText(cliente.getEmail());
        holder.tvDireccion.setText(cliente.getDireccion() + ", "+cliente.getMunicipio());
        holder.tvNotas.setText(cliente.getNotas());

        holder.itemView.setOnClickListener(v -> {
            if(listener != null){
                listener.onItemClick(cliente);
            }
        });
    }

    @Override
    public int getItemCount() {
        return  listaClientes != null? listaClientes.size() : 0;
    }

    static  class ClienteViewHolder extends RecyclerView.ViewHolder{
        TextView tvNombre, tvTelefono, tvEmail, tvDireccion, tvNotas;

        public ClienteViewHolder(@NonNull View itemView){
            super(itemView);

            tvNombre = itemView.findViewById(R.id.tv_item_nombre);
            tvTelefono = itemView.findViewById(R.id.tv_item_telefono);
            tvEmail = itemView.findViewById(R.id.tv_item_email);
            tvDireccion = itemView.findViewById(R.id.tv_item_direccion);
            tvNotas = itemView.findViewById(R.id.tv_dialog_notas);

        }
    }
}
