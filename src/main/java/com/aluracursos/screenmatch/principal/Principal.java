package com.aluracursos.screenmatch.principal;

import com.aluracursos.screenmatch.model.DatosEpisodio;
import com.aluracursos.screenmatch.model.DatosSerie;
import com.aluracursos.screenmatch.model.DatosTemporada;
import com.aluracursos.screenmatch.model.Episodio;
import com.aluracursos.screenmatch.service.ConsumoAPI;
import com.aluracursos.screenmatch.service.ConvierteDatos;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class Principal {

    private Scanner sc = new Scanner(System.in);
    private ConsumoAPI consumoAPI = new ConsumoAPI();
    private final String URL_BASE = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=df4de7d6";
    private ConvierteDatos conversor = new ConvierteDatos();

    public void mostrarMenu() {
        var opcion = -1;

        while (opcion != 0) {
            var menu = """
                    1 - Buscar series
                    2 - Buscar episodios
                                        
                    0 - salir 
                    """;

            opcion = sc.nextInt();
            sc.nextLine();

            switch (opcion) {
                case 1:
                    buscarSerie();
                    break;
                case 2:
                    buscarEpisodiosPorSerie();
                    break;
                case 0:
                    System.out.println("Cerrando aplicación");
                    break;
                default:
                    System.out.println("Opción invalida");
            }
        }
    }

    private DatosSerie buscarSerie() {
        System.out.println("Escribir el nombre de la serie que desea buscar");
        var nombreSerie = sc.nextLine();
        var json = consumoAPI.obtenerDatos(URL_BASE + URLEncoder.encode(nombreSerie, StandardCharsets.UTF_8) + API_KEY);
        var datos = conversor.obtenerDatos(json, DatosSerie.class);

        return datos;
    }

    public void buscarEpisodiosPorSerie() {
        var datosSerie = buscarSerie();
        List<DatosTemporada> temporadas = new ArrayList<>();
        try {
            for (int i = 1; i <= datosSerie.totalDeTemporadas(); i++) {
                var json = consumoAPI.obtenerDatos(URL_BASE + URLEncoder.encode(datosSerie.titulo(), StandardCharsets.UTF_8) + "&Season=" + i + API_KEY);
                var datosTemporada = conversor.obtenerDatos(json, DatosTemporada.class);
                temporadas.add(datosTemporada);
            }
        } catch (NullPointerException e) {
            System.out.println("Error de serie: " + e.getMessage());
        }
        temporadas.forEach(System.out::println);
    }

}
