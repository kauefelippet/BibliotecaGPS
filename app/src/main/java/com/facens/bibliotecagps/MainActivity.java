package com.facens.bibliotecagps;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Classe principal da aplicação que permite buscar informações de livros através da API do Google Books.
 */
public class MainActivity extends AppCompatActivity {

    private FusedLocationProviderClient fusedLocationClient;
    private FirebaseFirestore db;
    private String tituloTemp = "";
    private String autorTemp = "";
    private String editoraTemp = "";
    private String anoTemp = "";
    private double latitudeTemp = 0.0;
    private double longitudeTemp = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        db = FirebaseFirestore.getInstance();
    }

    /**
     * Realiza a busca do livro na API do Google Books em uma thread separada.
     *
     * @param view A View que disparou o evento (botão de busca).
     */
    public void buscarLivroNaApi(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    EditText edtBuscaLivro = findViewById(R.id.edtBuscaLivro);
                    String termoBusca = edtBuscaLivro.getText().toString().trim().replace(" ", "+");

                    if (termoBusca.isEmpty()) {
                        return;
                    }

                    URL url = new URL("https://www.googleapis.com/books/v1/volumes?q=" + termoBusca);
                    HttpURLConnection conexao = (HttpURLConnection) url.openConnection();
                    conexao.setRequestMethod("GET");

                    int responseCode = conexao.getResponseCode();
                    if (responseCode == 200) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(conexao.getInputStream()));
                        String inputLine;
                        StringBuilder response = new StringBuilder();

                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        in.close();

                        String resultadoJson = response.toString();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                TextView txtResultadoBusca = findViewById(R.id.txtResultadoBusca);
                                try {
                                    JSONObject jsonCompleto = new JSONObject(resultadoJson);

                                    if (jsonCompleto.has("items")) {
                                        JSONArray itens = jsonCompleto.getJSONArray("items");

                                        JSONObject primeiroLivro = itens.getJSONObject(0).getJSONObject("volumeInfo");

                                        String titulo = primeiroLivro.optString("title", "Título não disponível");

                                        String autor = "Autor não informado";
                                        if (primeiroLivro.has("authors")) {
                                            autor = primeiroLivro.getJSONArray("authors").getString(0);
                                        }

                                        String ano = primeiroLivro.optString("publishedDate", "Sem ano");
                                        String editora = primeiroLivro.optString("publisher", "Sem editora");

                                        tituloTemp = titulo;
                                        autorTemp = autor;
                                        anoTemp = ano;
                                        editoraTemp = editora;

                                        String textoFormatado = "Título: " + titulo + "\n" +
                                                "Autor: " + autor + "\n" +
                                                "Editora: " + editora + "\n" +
                                                "Ano: " + ano;

                                        txtResultadoBusca.setText(textoFormatado);
                                    } else {
                                        txtResultadoBusca.setText("Nenhum livro encontrado para o termo digitado.");
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                    txtResultadoBusca.setText("Erro ao processar dados do livro.");
                                }
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(() -> {
                        TextView txtResultadoBusca = findViewById(R.id.txtResultadoBusca);
                        txtResultadoBusca.setText("Erro na conexão com a API.");
                    });
                }
            }
        }).start();
    }

    public void capturarLocalizacao(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 200);

        } else {
            //puxa a última localização conhecida do celular
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();

                        latitudeTemp = latitude;
                        longitudeTemp = longitude;

                        TextView txtCoordenadas = findViewById(R.id.txtCoordenadas);
                        txtCoordenadas.setText("Latitude: " + latitude + "\nLongitude: " + longitude);

                        Toast.makeText(MainActivity.this, "Localização capturada com sucesso!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Não foi possível obter a localização. Verifique se o GPS está ligado.", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    public void salvarLivroNoFirebase(View view) {
        if (tituloTemp.isEmpty() || latitudeTemp == 0.0) {
            Toast.makeText(this, "Por favor, busque um livro e pegue a localização primeiro!", Toast.LENGTH_LONG).show();
            return;
        }

        EditText edtLocal = findViewById(R.id.edtLocalEncontrado);
        EditText edtStatus = findViewById(R.id.edtStatusLeitura);
        EditText edtObs = findViewById(R.id.edtObservacao);

        Livro novoLivro = new Livro();
        novoLivro.setTitulo(tituloTemp);
        novoLivro.setAutor(autorTemp);
        novoLivro.setAnoPublicacao(anoTemp);
        novoLivro.setEditora(editoraTemp);
        novoLivro.setLatitude(latitudeTemp);
        novoLivro.setLongitude(longitudeTemp);
        novoLivro.setSituacaoEncontrado(edtLocal.getText().toString());
        novoLivro.setStatusLeitura(edtStatus.getText().toString());
        novoLivro.setObservacao(edtObs.getText().toString());

        db.collection("livros")
                .add(novoLivro)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(MainActivity.this, "Livro salvo com sucesso no Firebase!", Toast.LENGTH_SHORT).show();

                    edtLocal.setText("");
                    edtStatus.setText("");
                    edtObs.setText("");
                    TextView txtResultadoBusca = findViewById(R.id.txtResultadoBusca);
                    txtResultadoBusca.setText("O resultado aparecerá aqui...");
                    tituloTemp = "";
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(MainActivity.this, "Erro ao salvar: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}
