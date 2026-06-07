package com.facens.bibliotecagps;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class LivroAdapter extends RecyclerView.Adapter<LivroAdapter.LivroViewHolder> {

    private List<Livro> listaLivros;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Livro livro);
    }

    public LivroAdapter(List<Livro> listaLivros, OnItemClickListener listener) {
        this.listaLivros = listaLivros;
        this.listener = listener;
    }

    @Override
    public LivroViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Cria um item_livro.xml para cada item da lista
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_livro, parent, false);
        return new LivroViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LivroViewHolder holder, int position) {
        Livro livro = listaLivros.get(position);
        holder.tvTitulo.setText(livro.getTitulo());
        holder.tvAutor.setText(livro.getAutor());

        String editoraAno = livro.getEditora() + " - " + livro.getAnoPublicacao();
        holder.tvEditora.setText(editoraAno);

        holder.itemView.setOnClickListener(v -> listener.onItemClick(livro));
    }

    @Override
    public int getItemCount() {
        return listaLivros.size();
    }

    public static class LivroViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo, tvAutor, tvEditora;

        public LivroViewHolder(View itemView) {
            super(itemView);
            tvTitulo  = itemView.findViewById(R.id.tvTitulo);
            tvAutor   = itemView.findViewById(R.id.tvAutor);
            tvEditora = itemView.findViewById(R.id.tvEditora);
        }
    }
}