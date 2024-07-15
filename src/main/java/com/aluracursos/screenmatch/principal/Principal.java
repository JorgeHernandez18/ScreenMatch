package com.aluracursos.screenmatch.principal;

import com.aluracursos.screenmatch.model.DatosSerie;
import com.aluracursos.screenmatch.model.DatosTemporada;
import com.aluracursos.screenmatch.model.Episodio;
import com.aluracursos.screenmatch.model.Serie;
import com.aluracursos.screenmatch.repository.SerieRepository;
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

    private List<DatosSerie> datosSeries = new ArrayList<>();
    private List<Serie> series = new ArrayList<>();
    private SerieRepository repositorio;

    public Principal(SerieRepository repository) {
        this.repositorio = repository;
    }

    public void mostrarMenu() {
        var opcion = -1;

        while (opcion != 0) {
            var menu = """
                    1 - Buscar series
                    2 - Buscar episodios
                    3 - Mostrar series buscadas
                                   
                    0 - salir 
                    """;
            System.out.println(menu);
            opcion = sc.nextInt();
            sc.nextLine();

            switch (opcion) {
                case 1:
                    buscarSerieWeb();
                    break;
                case 2:
                    buscarEpisodiosPorSerie();
                    break;
                case 3:
                    mostrarSeriesBuscadas();
                    break;
                case 0:
                    System.out.println("Cerrando aplicación");
                    break;
                default:
                    System.out.println("Opción invalida");
            }
        }
    }

    private void buscarSerieWeb() {
        var datos = getDatosSerie();
        Serie serie = new Serie(datos);
        repositorio.save(serie);
        System.out.println(datos);
    }

    private DatosSerie getDatosSerie() {
        System.out.println("Escribir el nombre de la serie que desea buscar");
        var nombreSerie = sc.nextLine();
        var json = consumoAPI.obtenerDatos(URL_BASE + URLEncoder.encode(nombreSerie, StandardCharsets.UTF_8) + API_KEY);
        var datos = conversor.obtenerDatos(json, DatosSerie.class);

        return datos;
    }

    public void buscarEpisodiosPorSerie() {
        mostrarSeriesBuscadas();
        System.out.println("Escriba el nombre de la serie que desea ver los episodios");
        var nombreSerie = sc.nextLine();
        Optional<Serie> serie = series.stream()
                .filter(s -> s.getTitulo().toLowerCase().contains(nombreSerie.toLowerCase()))
                .findFirst();
        if(serie.isPresent()){
            var serieBuscada = serie.get();
            List<DatosTemporada> temporadas = new ArrayList<>();
            try {
                for (int i = 1; i <= serieBuscada.getTotalDeTemporadas(); i++) {
                    var json = consumoAPI.obtenerDatos(URL_BASE + URLEncoder.encode(serieBuscada.getTitulo(), StandardCharsets.UTF_8) + "&Season=" + i + API_KEY);
                    var datosTemporada = conversor.obtenerDatos(json, DatosTemporada.class);
                    temporadas.add(datosTemporada);
                }
            } catch (NullPointerException e) {
                System.out.println("Error de serie: " + e.getMessage());
            }
            temporadas.forEach(System.out::println);

            List<Episodio> episodios = temporadas.stream()
                    .flatMap(d -> d.episodios().stream()
                            .map(e -> new Episodio(d.numero(), e)))
                    .collect(Collectors.toList());

            serieBuscada.setEpisodios(episodios);
            repositorio.save(serieBuscada);
        }
    }

    private void mostrarSeriesBuscadas() {
        series = repositorio.findAll();
        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);
    }

}
