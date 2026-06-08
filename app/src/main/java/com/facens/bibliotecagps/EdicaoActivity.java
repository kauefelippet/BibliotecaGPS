package com.facens.bibliotecagps;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.List;

public class EdicaoActivity extends AppCompatActivity {

    private TextView tvTituloEdicao, tvAutorEdicao;
    private Spinner spinnerStatusEdicao;
    private EditText etObservacaoEdicao;
    private Button btnSalvarEdicao;

    private FirebaseFirestore db;
    private Livro livro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edicao);

        tvTituloEdicao      = findViewById(R.id.tvTituloEdicao);
        tvAutorEdicao       = findViewById(R.id.tvAutorEdicao);
        spinnerStatusEdicao = findViewById(R.id.spinnerStatusEdicao);
        etObservacaoEdicao  = findViewById(R.id.etObservacaoEdicao);
        btnSalvarEdicao     = findViewById(R.id.btnSalvarEdicao);

        db = FirebaseFirestore.getInstance();

        livro = (Livro) getIntent().getSerializableExtra("livro");

        tvTituloEdicao.setText(livro.getTitulo());
        tvAutorEdicao.setText(livro.getAutor());
        etObservacaoEdicao.setText(livro.getObservacao());

        configurarSpinner();

        btnSalvarEdicao.setOnClickListener(v -> salvarEdicao());
    }

    private void configurarSpinner() {
        List<String> opcoes = Arrays.asList("Quero ler", "Lendo", "Concluído");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, opcoes);
        spinnerStatusEdicao.setAdapter(adapter);

        int posicaoAtual = opcoes.indexOf(livro.getStatusLeitura());
        if (posicaoAtual >= 0) {
            spinnerStatusEdicao.setSelection(posicaoAtual);
        }
    }

    private void salvarEdicao() {
        String novoStatus    = spinnerStatusEdicao.getSelectedItem().toString();
        String novaObservacao = etObservacaoEdicao.getText().toString().trim();

        db.collection("livros").document(livro.getId())
                .update("statusLeitura", novoStatus,
                        "observacao", novaObservacao)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Livro atualizado!", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Erro ao atualizar: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }
}