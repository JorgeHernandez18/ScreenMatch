package com.aluracursos.screenmatch.principal;

import com.aluracursos.screenmatch.model.DatosEpisodio;
import com.aluracursos.screenmatch.model.DatosSerie;
import com.aluracursos.screenmatch.model.DatosTemporada;
import com.aluracursos.screenmatch.model.Episodio;
import com.aluracursos.screenmatch.service.ConsumoAPI;
import com.aluracursos.screenmatch.service.ConvierteDatos;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class Principal {

    private Scanner sc = new Scanner(System.in);
    private ConsumoAPI consumoAPI = new ConsumoAPI();
    private final String URL_BASE = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=df4de7d6";
    private ConvierteDatos conversor = new ConvierteDatos();

    public void mostrarMenu(){
        System.out.println("Escribir el nombre de la serie que desea buscar");
        var nombreSerie = sc.nextLine();
        var json = consumoAPI.obtenerDatos(URL_BASE+ URLEncoder.encode(nombreSerie, StandardCharsets.UTF_8) + API_KEY);
        var datos = conversor.obtenerDatos(json, DatosSerie.class);


        System.out.println("Datos de la serie: " + datos);

        var temporadas = buscarDatosTemporada(datos, json, nombreSerie);


        List<DatosEpisodio> datosEpisodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream())
                .collect(Collectors.toList());

        //Convirtiendo los datos a lista de tipo episodio
        List<Episodio> episodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream()
                        .map(d -> new Episodio(t.numero(),d)))
                .collect(Collectors.toList());


        //Agrupando las evaluaciones de cada serie por temporada con el map
        Map<Integer, Double> evaluacionesPorTemporada = episodios.stream()
                .filter(e -> e.getEvaluacion() > 0.0)
                .collect(Collectors.groupingBy(Episodio::getTemporada,
                        Collectors.averagingDouble(Episodio::getEvaluacion)));

        System.out.println("Evaluaciones por temporada " + evaluacionesPorTemporada);

        //Usamos estadisticas para ver las evaluaciones de los episodios
        DoubleSummaryStatistics est = episodios.stream()
                .filter(e -> e.getEvaluacion() > 0.0)
                .collect(Collectors.summarizingDouble(Episodio::getEvaluacion));

        System.out.println("Media de las evaluaciones: " + est.getAverage());
        System.out.println("Episodio mejor evaluado: " + est.getMax());
        System.out.println("Episodio peor evaluado: " + est.getMin());

    }

    public List<DatosTemporada> buscarDatosTemporada(DatosSerie datos, String json, String nombreSerie){
        List<DatosTemporada> temporadas = new ArrayList<>();
        try{
            for (int i = 1; i <= datos.totalDeTemporadas(); i++) {
                json = consumoAPI.obtenerDatos(URL_BASE + URLEncoder.encode(nombreSerie, StandardCharsets.UTF_8) + "&Season="+ i +API_KEY);
                var datosTemporada = conversor.obtenerDatos(json, DatosTemporada.class);
                temporadas.add(datosTemporada);
            }
        } catch (NullPointerException e){
            System.out.println("Error de serie: " + e.getMessage());
        }
        return temporadas;
    }

}
