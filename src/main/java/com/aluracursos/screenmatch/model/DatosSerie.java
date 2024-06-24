package com.aluracursos.screenmatch.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/*
* Se usa la anotación @JsonAlias para poder leer y convertir los datos que extraemos de la API
* externa a nuestro modelo de Java.
* Se podría usar tambien la anotación @JsonProperties, solo que esta ultima se usa para
* leer y escribir, en cambio, @JsonAlias se usa para leer solamente.*/
@JsonIgnoreProperties(ignoreUnknown = true)
public record DatosSerie(@JsonAlias("Title") String titulo,
                         @JsonAlias("totalSeasons") Integer totalDeTemporadas,
                         @JsonAlias("imdbRating") String evaluacion) {
}
