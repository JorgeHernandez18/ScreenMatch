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

//        System.out.println("<-------------------------------Top 5 episodios------------------------------->");
//        datosEpisodios.stream()
//                .filter(e -> !e.evaluacion().equalsIgnoreCase("N/A"))
//                .peek(e -> System.out.println("Primer filtro (N/A) " + e))
//                .sorted(Comparator.comparing(DatosEpisodio::evaluacion).reversed())
//                .peek(e -> System.out.println("Segundo filtro ordenacion (E>e)" + e))
//                .map(e -> e.titulo().toUpperCase())
//                .peek(e -> System.out.println("Tercer filtro MAYUSCULAS " + e))
//                .limit(5)
//                .forEach(System.out::println);

        //System.out.println("Imprimiendo todos los episodios con la temporada");
        //Convirtiendo los datos a lista de tipo episodio
        List<Episodio> episodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream()
                        .map(d -> new Episodio(t.numero(),d)))
                .collect(Collectors.toList());

        //episodios.forEach(System.out::println);


        //Busqueda de episodios a partir de x año
//        System.out.println("Indica el año a partir del cual deseas ver los episodios");
//        var fecha = sc.nextInt();
//        sc.nextLine();

        //LocalDate fechaBusqueda = LocalDate.of(fecha, 1, 1);

        //Se formatea la fecha para que aparezca de la forma que me guste
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

       /* episodios.stream()
                .filter(e -> e.getFechaLanzamiento() != null && e.getFechaLanzamiento().isAfter(fechaBusqueda))
                .forEach(e -> System.out.println(
                        "Temporada " + e.getTemporada() +
                                " - Episodio " + e.getTitulo() +
                                    " - Fecha lanzamiento " +e.getFechaLanzamiento().format(dtf)
                ));*/

        //Busca episodios por pedazo de titulo

        /*System.out.println("Escribe el pedazo de titulo que conoces");
        var pedazo = sc.nextLine();

        Optional<Episodio> episodioBuscado = episodios.stream()
                .filter(e -> e.getTitulo().toUpperCase().contains(pedazo.toUpperCase()))
                .findFirst();

        if(episodioBuscado.isPresent()){
            System.out.println("El episodio es: " + episodioBuscado.get());
        } else {
            System.out.println("Episodio no encontrado");
        }*/

        //Agrupando las evaluaciones de cada serie por temporada con el map
        Map<Integer, Double> evaluacionesPorTemporada = episodios.stream()
                .filter(e -> e.getEvaluacion() > 0.0)
                .collect(Collectors.groupingBy(Episodio::getTemporada,
                        Collectors.averagingDouble(Episodio::getEvaluacion)));

        System.out.println("Evaluaciones por temporada " + evaluacionesPorTemporada);
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
