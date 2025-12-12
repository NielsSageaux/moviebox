package com.example.moviebox.service;

import com.example.moviebox.dto.MovieDto;
import com.example.moviebox.dto.MovieResponseDto;
import com.example.moviebox.model.Movie;
import com.example.moviebox.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;
    private final TmdbService tmdbService;

    // Cherche sur TMDB mais NE SAUVEGARDE PAS automatiquement
    public MovieResponseDto searchMovies(String query) {
        MovieResponseDto response = tmdbService.searchMovies(query);

        // Filtre les résultats de qualité
        if (response != null && response.getResults() != null) {
            List<MovieDto> filtered = response.getResults().stream()
                    .filter(this::isValidMovie)  // ← Nouveau filtre
                    .collect(Collectors.toList());
            response.setResults(filtered);
        }

        return response;
    }

    // Sauvegarde MANUELLEMENT un film spécifique
    public Movie saveMovie(Long tmdbId) {
        // Vérifie si déjà sauvegardé
        Optional<Movie> existing = movieRepository.findByTmdbId(tmdbId);
        if (existing.isPresent()) {
            return existing.get();
        }

        // Récupère les détails complets depuis TMDB
        MovieDto movieDto = tmdbService.getMovieDetails(tmdbId);

        if (movieDto != null && isValidMovie(movieDto)) {
            return saveMovieFromDto(movieDto);
        }

        throw new RuntimeException("Film invalide ou introuvable");
    }

    // Filtre de qualité pour éviter les artefacts
    private boolean isValidMovie(MovieDto dto) {
        if (dto == null) return false;

        // Critères de filtrage
        return dto.getTitle() != null
                && !dto.getTitle().isEmpty()
                && dto.getReleaseDate() != null
                && !dto.getReleaseDate().isEmpty()
                && dto.getPosterPath() != null  // ← Doit avoir un poster
                && dto.getVoteCount() != null
                && dto.getVoteCount() > 10  // ← Au moins 10 votes (évite les obscurs)
                && dto.getPopularity() != null
                && dto.getPopularity() > 1.0;  // ← Minimum de popularité
    }

    // Conversion DTO → Entity
    private Movie saveMovieFromDto(MovieDto dto) {
        Movie movie = Movie.builder()
                .tmdbId(dto.getId())
                .title(dto.getTitle())
                .director(null)  // On remplira plus tard avec les crédits
                .releaseYear(extractYear(dto.getReleaseDate()))
                .synopsis(dto.getOverview())
                .posterUrl(dto.getPosterPath() != null ?
                        "https://image.tmdb.org/t/p/w500" + dto.getPosterPath() : null)
                .build();

        return movieRepository.save(movie);
    }

    private Integer extractYear(String releaseDate) {
        if (releaseDate != null && releaseDate.length() >= 4) {
            return Integer.parseInt(releaseDate.substring(0, 4));
        }
        return null;
    }

    // Liste tous les films sauvegardés dans H2
    public List<Movie> getAllSavedMovies() {
        return movieRepository.findAll();
    }

    // Récupère un film depuis H2 par son ID local
    public Optional<Movie> getSavedMovieById(Long id) {
        return movieRepository.findById(id);
    }

    // Cherche dans les films sauvegardés
    public List<Movie> searchSavedMovies(String title) {
        return movieRepository.findByTitleContainingIgnoreCase(title);
    }
}
