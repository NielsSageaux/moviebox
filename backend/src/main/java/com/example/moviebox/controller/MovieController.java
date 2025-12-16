package com.example.moviebox.controller;

import com.example.moviebox.dto.MovieDto;
import com.example.moviebox.dto.MovieResponseDto;
import com.example.moviebox.model.Movie;
import com.example.moviebox.service.MovieService;
import com.example.moviebox.service.TmdbService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
// Autorise toutes les origines en développement pour éviter les erreurs CORS
@CrossOrigin
public class MovieController {

    private final TmdbService tmdbService;
    private final MovieService movieService;

    // Films populaires TMDB (pas de sauvegarde)
    @GetMapping("/popular")
    public MovieResponseDto getPopularMovies() {
        return tmdbService.getPopularMovies();
    }

    // Recherche TMDB (filtrée, pas de sauvegarde auto)
    @GetMapping("/search")
    public MovieResponseDto searchMovies(@RequestParam String query) {
        return movieService.searchMovies(query);
    }

    // Détails TMDB uniquement
    @GetMapping("/tmdb/{id}")
    public MovieDto getMovieDetails(@PathVariable Long id) {
        return tmdbService.getMovieDetails(id);
    }

    // NOUVEAU : Sauvegarder un film spécifique
    @PostMapping("/save/{tmdbId}")
    public Movie saveMovie(@PathVariable Long tmdbId) {
        return movieService.saveMovie(tmdbId);
    }

    // Films sauvegardés localement
    @GetMapping("/saved")
    public List<Movie> getAllSavedMovies() {
        return movieService.getAllSavedMovies();
    }

    @GetMapping("/saved/{id}")
    public Movie getSavedMovieById(@PathVariable Long id) {
        return movieService.getSavedMovieById(id)
                .orElseThrow(() -> new RuntimeException("Film non trouvé"));
    }

    @GetMapping("/saved/search")
    public List<Movie> searchSavedMovies(@RequestParam String title) {
        return movieService.searchSavedMovies(title);
    }

    public MovieController(TmdbService tmdbService, MovieService movieService) {
        this.tmdbService = tmdbService;
        this.movieService = movieService;
    }
}
