package com.facens.bibliotecagps;

import java.io.Serializable;

public class Livro implements Serializable {

    // Identificador para o Firebase
    private String id;

    // Dados que virão da API de Livros
    private String titulo;
    private String autor;
    private String anoPublicacao;
    private String editora;

    // Dados que virão do GPS do celular
    private double latitude;
    private double longitude;

    // Dados que o usuário vai digitar na tela
    private String situacaoEncontrado;
    private String statusLeitura;
    private String observacao;


    public Livro() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getAnoPublicacao() {
        return anoPublicacao;
    }

    public void setAnoPublicacao(String anoPublicacao) {
        this.anoPublicacao = anoPublicacao;
    }

    public String getEditora() {
        return editora;
    }

    public void setEditora(String editora) {
        this.editora = editora;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getSituacaoEncontrado() {
        return situacaoEncontrado;
    }

    public void setSituacaoEncontrado(String situacaoEncontrado) {
        this.situacaoEncontrado = situacaoEncontrado;
    }

    public String getStatusLeitura() {
        return statusLeitura;
    }

    public void setStatusLeitura(String statusLeitura) {
        this.statusLeitura = statusLeitura;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}