package com.aluracursos.screenmatch.service;

import com.aluracursos.screenmatch.model.translater.Data;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class TranslateAPI {

    public static String traducir(String texto) {
        Dotenv dotenv = Dotenv.configure().load();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://deep-translate1.p.rapidapi.com/language/translate/v2"))
                .header("x-rapidapi-key", dotenv.get("API_KEY"))
                .header("x-rapidapi-host", "deep-translate1.p.rapidapi.com")
                .header("Content-Type", "application/json")
                .method("POST", HttpRequest.BodyPublishers.ofString("{\"q\":\"" + texto + "\",\"source\":\"en\",\"target\":\"es\"}"))
                .build();
        HttpResponse<String> response = null;
        try {
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        return limpiarTraduccion(response.body());
    }

    public static String limpiarTraduccion(String texto) {
        var datos = new ConvierteDatos().obtenerDatos(texto, Data.class);
        return datos.translate().traduccion().traduccionSpain();
    }
}
