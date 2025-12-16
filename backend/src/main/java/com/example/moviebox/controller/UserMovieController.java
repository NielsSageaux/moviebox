package com.example.moviebox.controller;

import com.example.moviebox.model.UserMovie;
import com.example.moviebox.service.UserMovieService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user-movies")
@CrossOrigin(origins = "http://localhost:4200") // Angular
public class UserMovieController {

    private final UserMovieService userMovieService;

    public UserMovieController(UserMovieService userMovieService) {
        this.userMovieService = userMovieService;
    }

    /**
     * GET /api/user-movies/watchlist
     * Récupère la watchlist (films à voir)
     */
    @GetMapping("/watchlist")
    public ResponseEntity<List<UserMovie>> getWatchlist() {
        List<UserMovie> watchlist = userMovieService.getWatchlist();
        return ResponseEntity.ok(watchlist);
    }

    /**
     * POST /api/user-movies/watchlist/{tmdbId}
     * Ajoute un film à la watchlist
     */
    @PostMapping("/watchlist/{tmdbId}")
    public ResponseEntity<UserMovie> addToWatchlist(@PathVariable Long tmdbId) {
        try {
            UserMovie userMovie = userMovieService.addToWatchlist(tmdbId);
            return ResponseEntity.status(HttpStatus.CREATED).body(userMovie);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * GET /api/user-movies/rated
     * Récupère les films notés (triés par note décroissante)
     */
    @GetMapping("/rated")
    public ResponseEntity<List<UserMovie>> getRatedMovies() {
        List<UserMovie> ratedMovies = userMovieService.getRatedMovies();
        return ResponseEntity.ok(ratedMovies);
    }

    /**
     * POST /api/user-movies/rate/{tmdbId}
     * Note un film (1-10)
     * Body JSON: { "rating": 8.5 }
     */
    @PostMapping("/rate/{tmdbId}")
    public ResponseEntity<UserMovie> rateMovie(
            @PathVariable Long tmdbId,
            @RequestBody RatingRequest request
    ) {
        try {
            UserMovie userMovie = userMovieService.rateMovie(tmdbId, request.getRating());
            return ResponseEntity.ok(userMovie);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * DELETE /api/user-movies/{tmdbId}
     * Supprime un film de toutes les listes
     */
    @DeleteMapping("/{tmdbId}")
    public ResponseEntity<Void> removeFromList(@PathVariable Long tmdbId) {
        try {
            userMovieService.removeFromList(tmdbId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * GET /api/user-movies
     * Récupère TOUS les films de l'utilisateur (watchlist + rated)
     */
    @GetMapping
    public ResponseEntity<List<UserMovie>> getAllUserMovies() {
        List<UserMovie> allMovies = userMovieService.getAllUserMovies();
        return ResponseEntity.ok(allMovies);
    }

    /**
     * DTO pour recevoir la note depuis Angular
     */
    public static class RatingRequest {
        private Double rating;

        public RatingRequest() {}

        public RatingRequest(Double rating) {
            this.rating = rating;
        }

        public Double getRating() {
            return rating;
        }

        public void setRating(Double rating) {
            this.rating = rating;
        }
    }
}
