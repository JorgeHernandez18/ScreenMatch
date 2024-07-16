package com.aluracursos.screenmatch.service;

import com.aluracursos.screenmatch.dto.EpisodioDTO;
import com.aluracursos.screenmatch.dto.SerieDTO;
import com.aluracursos.screenmatch.model.Categoria;
import com.aluracursos.screenmatch.model.Serie;
import com.aluracursos.screenmatch.repository.SerieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SerieService {

    @Autowired
    private SerieRepository serieRepository;
    public List<SerieDTO> getSeries(){
        return convertirDTO(serieRepository.findAll());
    }

    public List<SerieDTO> getTop5() {
        return convertirDTO(serieRepository.findTop5ByOrderByEvaluacionDesc());
    }

    public List<SerieDTO> obtenerLanzamientosMasRecientes(){
        return convertirDTO(serieRepository.lanzamientosMasRecientes());
    }

    public SerieDTO obtenerPorId(Long id) {
        Optional<Serie> serie = serieRepository.findById(id);
        if (serie.isPresent()){
            Serie s = serie.get();
            return new SerieDTO(s.getId(), s.getTitulo(), s.getTotalDeTemporadas(), s.getEvaluacion(), s.getGenero(),
                    s.getPoster(), s.getActores(), s.getSinopsis());
        }
        return null;
    }

    public List<EpisodioDTO> obtenerTodasLasTemporadas(Long id) {
        Optional<Serie> serie = serieRepository.findById(id);
        if (serie.isPresent()){
            Serie s = serie.get();
            return s.getEpisodios().stream().map(e -> new EpisodioDTO(e.getTemporada(),e.getTitulo(),
                    e.getNumeroEpisodio())).collect(Collectors.toList());
        }
        return null;
    }

    public List<EpisodioDTO> obtenerTemporadasPorNumero(Long id, Long numeroTemporada) {
        return serieRepository.obtenerTemporadasPorNumero(id,numeroTemporada).stream()
                .map(e -> new EpisodioDTO(e.getTemporada(),e.getTitulo(),
                        e.getNumeroEpisodio())).collect(Collectors.toList());
    }

    public List<SerieDTO> obtenerSeriesPorCategoria(String nombreGenero) {
        Categoria categoria = Categoria.fromEspanol(nombreGenero);
        return convertirDTO(serieRepository.findByGenero(categoria));
    }

    public List<SerieDTO> convertirDTO(List<Serie> serie){
        return serie.stream()
                .map(s -> new SerieDTO(s.getId(), s.getTitulo(), s.getTotalDeTemporadas(), s.getEvaluacion(),
                        s.getGenero(), s.getSinopsis(), s.getPoster(), s.getActores()))
                .collect(Collectors.toList());
    }
}
