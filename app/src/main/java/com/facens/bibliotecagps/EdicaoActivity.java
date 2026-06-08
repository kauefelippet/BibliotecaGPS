package com.facens.bibliotecagps;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.List;

public class EdicaoActivity extends AppCompatActivity {

    private static final int CODIGO_PERMISSAO_LOCALIZACAO = 100;

    private TextView tvTituloEdicao, tvAutorEdicao, tvCoordenadasEdicao;
    private Spinner spinnerStatusEdicao;
    private EditText etObservacaoEdicao;
    private Button btnAtualizarLocalizacao, btnSalvarEdicao;

    private FusedLocationProviderClient fusedLocationClient;
    private FirebaseFirestore db;
    private Livro livro;

    private double latitudeAtual;
    private double longitudeAtual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edicao);

        tvTituloEdicao          = findViewById(R.id.tvTituloEdicao);
        tvAutorEdicao           = findViewById(R.id.tvAutorEdicao);
        tvCoordenadasEdicao     = findViewById(R.id.tvCoordenadasEdicao);
        spinnerStatusEdicao     = findViewById(R.id.spinnerStatusEdicao);
        etObservacaoEdicao      = findViewById(R.id.etObservacaoEdicao);
        btnAtualizarLocalizacao = findViewById(R.id.btnAtualizarLocalizacao);
        btnSalvarEdicao         = findViewById(R.id.btnSalvarEdicao);

        db = FirebaseFirestore.getInstance();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        livro = (Livro) getIntent().getSerializableExtra("livro");

        // Preenche os campos com os dados atuais do livro
        tvTituloEdicao.setText(livro.getTitulo());
        tvAutorEdicao.setText(livro.getAutor());
        etObservacaoEdicao.setText(livro.getObservacao());

        latitudeAtual  = livro.getLatitude();
        longitudeAtual = livro.getLongitude();
        atualizarTextoCoordenadas();

        configurarSpinner();

        btnAtualizarLocalizacao.setOnClickListener(v -> solicitarLocalizacao());
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

    private void atualizarTextoCoordenadas() {
        if (latitudeAtual == 0.0 && longitudeAtual == 0.0) {
            tvCoordenadasEdicao.setText("Nenhuma localização registrada");
        } else {
            tvCoordenadasEdicao.setText("Lat: " + latitudeAtual + " | Lon: " + longitudeAtual);
        }
    }

    private void solicitarLocalizacao() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    CODIGO_PERMISSAO_LOCALIZACAO);
        } else {
            capturarLocalizacao();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CODIGO_PERMISSAO_LOCALIZACAO) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                capturarLocalizacao();
            } else {
                Toast.makeText(this,
                        "Permissão negada. Localização não será atualizada.",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private void capturarLocalizacao() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                latitudeAtual  = location.getLatitude();
                longitudeAtual = location.getLongitude();
                atualizarTextoCoordenadas();
                Toast.makeText(this, "Localização atualizada!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this,
                        "Não foi possível obter a localização. Tente novamente.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void salvarEdicao() {
        String novoStatus     = spinnerStatusEdicao.getSelectedItem().toString();
        String novaObservacao = etObservacaoEdicao.getText().toString().trim();

        db.collection("livros").document(livro.getId())
                .update(
                        "statusLeitura", novoStatus,
                        "observacao",    novaObservacao,
                        "latitude",      latitudeAtual,
                        "longitude",     longitudeAtual
                )
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