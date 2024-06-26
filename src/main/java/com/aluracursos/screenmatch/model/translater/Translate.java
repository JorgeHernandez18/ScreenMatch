package com.aluracursos.screenmatch.model.translater;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Translate(@JsonAlias("translations") Translated traduccion) {
}
