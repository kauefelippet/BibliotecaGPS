package com.facens.bibliotecagps;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LivroSalvoAdapter extends RecyclerView.Adapter<LivroSalvoAdapter.ViewHolder> {

    private List<Livro> listaLivros;
    private OnItemClickListener listener;
    private OnItemLongClickListener longListener;

    public interface OnItemClickListener {
        void onItemClick(Livro livro);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(Livro livro);
    }

    public LivroSalvoAdapter(List<Livro> listaLivros,
                             OnItemClickListener listener,
                             OnItemLongClickListener longListener) {
        this.listaLivros  = listaLivros;
        this.listener     = listener;
        this.longListener = longListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_livro_salvo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Livro livro = listaLivros.get(position);

        holder.tvTitulo.setText(livro.getTitulo());
        holder.tvAutor.setText(livro.getAutor());
        holder.tvStatus.setText("Status: " + livro.getStatusLeitura());
        holder.tvSituacao.setText("Situação: " + livro.getSituacaoEncontrado());
        holder.tvCoordenadas.setText("Lat: " + livro.getLatitude() + " | Lon: " + livro.getLongitude());

        // Clique curto — abre edição
        holder.itemView.setOnClickListener(v -> listener.onItemClick(livro));

        // Clique longo — abre exclusão
        holder.itemView.setOnLongClickListener(v -> {
            longListener.onItemLongClick(livro);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return listaLivros.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo, tvAutor, tvStatus, tvSituacao, tvCoordenadas;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitulo      = itemView.findViewById(R.id.tvTituloSalvo);
            tvAutor       = itemView.findViewById(R.id.tvAutorSalvo);
            tvStatus      = itemView.findViewById(R.id.tvStatusSalvo);
            tvSituacao    = itemView.findViewById(R.id.tvSituacaoSalvo);
            tvCoordenadas = itemView.findViewById(R.id.tvCoordenadasSalvo);
        }
    }
}