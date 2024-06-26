package com.aluracursos.screenmatch.model;

public enum Categoria {
    ACCION("Action"),
    ROMANCE("Romance"),
    COMEDIA("Comedy"),
    CRIMEN("Crime"),
    DRAMA("Drama");

    private String categoriaOmdb;

    Categoria (String categoriaOmdb){
        this.categoriaOmdb = categoriaOmdb;
    }
}
