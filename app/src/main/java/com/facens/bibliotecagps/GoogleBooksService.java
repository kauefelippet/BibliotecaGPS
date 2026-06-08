package com.facens.bibliotecagps;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GoogleBooksService {

    @GET("volumes")
    Call<GoogleBooksResposta> buscarLivros(
            @Query("q") String termo,
            @Query("key") String apiKey);
}
