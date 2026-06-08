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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class CadastroActivity extends AppCompatActivity {

    private static final int CODIGO_PERMISSAO_LOCALIZACAO = 100;

    private TextView tvTituloCadastro, tvAutorCadastro, tvCoordenadas;
    private Spinner spinnerSituacao, spinnerStatus;
    private EditText edtObservacao;
    private Button btnObterLocalizacao, btnSalvar;

    private FusedLocationProviderClient fusedLocationClient;
    private FirebaseFirestore db;

    private Livro livroSelecionado;
    private double latitudeAtual = 0.0;
    private double longitudeAtual = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        // Vinculo dos componentes da interface
        tvTituloCadastro = findViewById(R.id.tvTituloCadastro);
        tvAutorCadastro = findViewById(R.id.tvAutorCadastro);
        tvCoordenadas = findViewById(R.id.tvCoordenadas);
        spinnerSituacao = findViewById(R.id.spinnerSituacao);
        spinnerStatus = findViewById(R.id.spinnerStatus);
        edtObservacao = findViewById(R.id.etObservacao);
        btnObterLocalizacao = findViewById(R.id.btnObterLocalizacao);
        btnSalvar = findViewById(R.id.btnSalvar);

        // Localização e Firestore
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        db = FirebaseFirestore.getInstance();

        // Recupera o Livro via Intent
        livroSelecionado = (Livro) getIntent().getSerializableExtra("livro");
        tvTituloCadastro.setText(livroSelecionado.getTitulo());
        tvAutorCadastro.setText(livroSelecionado.getAutor());

        configurarSpinners();

        btnObterLocalizacao.setOnClickListener(v -> solicitarLocalizacao());
        btnSalvar.setOnClickListener(v -> salvarLivro());
    }

    private void configurarSpinners() {
        ArrayAdapter<String> adapterSituacao = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.situacao_encontrado));
        spinnerSituacao.setAdapter(adapterSituacao);

        ArrayAdapter<String> adapterStatus = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.status_leitura));
        spinnerStatus.setAdapter(adapterStatus);
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
                        "Permissão negada. O livro será salvo sem localização.",
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
                tvCoordenadas.setText("Lat: " + latitudeAtual + " | Lon: " + longitudeAtual);
            } else {
                Toast.makeText(this,
                        "Não foi possível obter a localização. Tente novamente.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void salvarLivro() {
        if (livroSelecionado == null) {
            Toast.makeText(this, "Erro: nenhum livro selecionado.", Toast.LENGTH_SHORT).show();
            return;
        }

        livroSelecionado.setLatitude(latitudeAtual);
        livroSelecionado.setLongitude(longitudeAtual);
        livroSelecionado.setSituacaoEncontrado(spinnerSituacao.getSelectedItem().toString());
        livroSelecionado.setStatusLeitura(spinnerStatus.getSelectedItem().toString());
        livroSelecionado.setObservacao(edtObservacao.getText().toString().trim());

        DocumentReference ref = db.collection("livros").document();
        livroSelecionado.setId(ref.getId());

        ref.set(livroSelecionado)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Livro salvo com sucesso!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Erro ao salvar: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}