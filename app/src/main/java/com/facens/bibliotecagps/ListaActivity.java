package com.facens.bibliotecagps;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ListaActivity extends AppCompatActivity {

    private RecyclerView recyclerLivrosSalvos;
    private TextView tvListaVazia;

    private List<Livro> listaLivros = new ArrayList<>();
    private LivroSalvoAdapter adapter;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista);

        recyclerLivrosSalvos = findViewById(R.id.recyclerLivrosSalvos);
        tvListaVazia = findViewById(R.id.tvListaVazia);

        db = FirebaseFirestore.getInstance();

        adapter = new LivroSalvoAdapter(
                listaLivros,
                this::abrirEdicao,
                this::confirmarExclusao
        );

        recyclerLivrosSalvos.setLayoutManager(new LinearLayoutManager(this));
        recyclerLivrosSalvos.setAdapter(adapter);

        carregarLivros();
    }

    private void carregarLivros() {
        db.collection("livros").get()
                .addOnSuccessListener(querySnapshot -> {
                    listaLivros.clear();

                    for (QueryDocumentSnapshot documento : querySnapshot) {
                        Livro livro = documento.toObject(Livro.class);
                        listaLivros.add(livro);
                    }

                    adapter.notifyDataSetChanged();

                    if (listaLivros.isEmpty()) {
                        tvListaVazia.setVisibility(View.VISIBLE);
                    } else {
                        tvListaVazia.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Erro ao carregar livros: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }

    private void abrirEdicao(Livro livro) {
        Intent intent = new Intent(this, EdicaoActivity.class);
        intent.putExtra("livro", livro);
        startActivityForResult(intent, 1);
    }

    private void confirmarExclusao(Livro livro) {
        new AlertDialog.Builder(this)
                .setTitle("Excluir livro")
                .setMessage("Deseja excluir \"" + livro.getTitulo() + "\"?")
                .setPositiveButton("Excluir", (dialog, which) -> excluirLivro(livro))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void excluirLivro(Livro livro) {
        db.collection("livros").document(livro.getId()).delete()
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Livro excluído.", Toast.LENGTH_SHORT).show();
                    carregarLivros();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Erro ao excluir: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            carregarLivros();
        }
    }
}