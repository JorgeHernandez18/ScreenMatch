package com.aluracursos.screenmatch.principal;

import com.aluracursos.screenmatch.model.DatosEpisodio;
import com.aluracursos.screenmatch.model.DatosSerie;
import com.aluracursos.screenmatch.model.DatosTemporada;
import com.aluracursos.screenmatch.service.ConsumoAPI;
import com.aluracursos.screenmatch.service.ConvierteDatos;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
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

        var json = consumoAPI.obtenerDatos(URL_BASE+ nombreSerie.replace(" ", "+") + API_KEY);

        var datos = conversor.obtenerDatos(json, DatosSerie.class);

        System.out.println("Datos de la serie: " + datos);

        var temporadas = buscarDatosTemporada(datos, json, nombreSerie);

        //Imprimo con un forEach la lista de DatosTemporada e imprimo solo los titulos de los episodios
        /*
        * temporadas.forEach(
                t -> t.episodios()
                        .forEach(e -> System.out.println(e.titulo()))
        );
        * */

        /*
        * Con la expresión lambda t -> t.episodios()
                        .forEach(e -> System.out.println(e.titulo()))
        * Estoy ahorrandome de hacer el siguiente codigo:
        * for(int i = 0; i < datos.totalTemporadas(); i++){
        *   List<DatosEpisodio> episodiosTemporada = temporadas.get(i).episodios();
        *   for(int j = 0; j < episodiosTemporada.size(); j++){
        *       System.out.println(episodiosTemporada.get(j).titulo());
        *   }
        * }
        *
        * ¡IMPRESIONANTE!
        * */


        List<DatosEpisodio> datosEpisodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream())
                .collect(Collectors.toList());
        //Uso el collect(Collectors.toList()) en lugar del toList() porque el toList() me retorna
        //Una lista inmutable, el collect no.

        System.out.println("Top 5 episodios");
        datosEpisodios.stream()
                .filter(e -> !e.evaluacion().equalsIgnoreCase("N/A"))
                .sorted(Comparator.comparing(DatosEpisodio::evaluacion).reversed())
                .limit(5)
                .forEach(System.out::println);
    }

    public List<DatosTemporada> buscarDatosTemporada(DatosSerie datos, String json, String nombreSerie){
        List<DatosTemporada> temporadas = new ArrayList<>();
        for (int i = 1; i <= datos.totalDeTemporadas(); i++) {
            json = consumoAPI.obtenerDatos(URL_BASE + nombreSerie.replace(" ", "+") + "&Season="+ i +API_KEY);
            var datosTemporada = conversor.obtenerDatos(json, DatosTemporada.class);
            temporadas.add(datosTemporada);
        }

        return temporadas;
    }
}
