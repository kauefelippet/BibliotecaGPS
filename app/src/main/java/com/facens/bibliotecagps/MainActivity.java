package com.facens.bibliotecagps;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private static final String API_KEY = BuildConfig.API_KEY;

    private EditText edtPesquisa;
    private Button btnVerLista, btnBuscar;
    private ProgressBar progressBar;
    private RecyclerView recyclerLivros;

    private List<Livro> listaLivros = new ArrayList<>();
    private LivroAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Vinculo dos componentes da interface
        edtPesquisa = findViewById(R.id.etPesquisa);
        btnVerLista = findViewById(R.id.btnVerLista);
        btnBuscar     = findViewById(R.id.btnBuscar);
        progressBar   = findViewById(R.id.progressBar);
        recyclerLivros = findViewById(R.id.recyclerLivros);

        // Config adapter
        adapter = new LivroAdapter(listaLivros, livro -> abrirCadastro(livro));
        recyclerLivros.setLayoutManager(new LinearLayoutManager(this));
        recyclerLivros.setAdapter(adapter);

        btnVerLista.setOnClickListener(v -> {
            startActivity(new Intent(this, ListaActivity.class));
        });

        btnBuscar.setOnClickListener(v -> {
            String termo = edtPesquisa.getText().toString().trim();
            if (termo.isEmpty()) {
                Toast.makeText(this, "Digite algo para buscar", Toast.LENGTH_SHORT).show();
                return;
            }
            buscarLivros(termo);
        });
    }

    private void buscarLivros(String termo) {
        progressBar.setVisibility(View.VISIBLE);
        listaLivros.clear();
        adapter.notifyDataSetChanged();

        // Verificar API Key
        if (API_KEY == null || API_KEY.isEmpty()) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "API_KEY não configurada. Verifique local.properties e o build.", Toast.LENGTH_LONG).show();
            return;
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.googleapis.com/books/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        GoogleBooksService service = retrofit.create(GoogleBooksService.class);

        service.buscarLivros(termo, API_KEY).enqueue(new Callback<GoogleBooksResposta>() {

            @Override
            public void onResponse(Call<GoogleBooksResposta> call, Response<GoogleBooksResposta> response) {
                progressBar.setVisibility(View.GONE);

                if (response.body() == null || response.body().getItems() == null) {
                    Toast.makeText(MainActivity.this, "Nenhum livro encontrado", Toast.LENGTH_SHORT).show();
                    return;
                }

                for (GoogleBooksResposta.Item item : response.body().getItems()) {
                    GoogleBooksResposta.VolumeInfo info = item.getVolumeInfo();

                    String titulo = info.getTitle() != null ? info.getTitle() : "Sem título";

                    String autores = "Autor desconhecido";
                    if (info.getAuthors() != null && !info.getAuthors().isEmpty()) {
                        autores = info.getAuthors().get(0);
                    }

                    String editora = info.getPublisher() != null ? info.getPublisher() : "Editora desconhecida";
                    String ano = info.getPublishedDate() != null ? info.getPublishedDate() : "Ano desconhecido";

                    Livro livro = new Livro();
                    livro.setTitulo(titulo);
                    livro.setAutor(autores);
                    livro.setEditora(editora);
                    livro.setAnoPublicacao(ano);

                    listaLivros.add(livro);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<GoogleBooksResposta> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "Erro: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void abrirCadastro(Livro livro) {
        Intent intent = new Intent(this, CadastroActivity.class);
        intent.putExtra("livro", livro);
        startActivity(intent);
    }
}