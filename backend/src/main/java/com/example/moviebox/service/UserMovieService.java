package com.example.moviebox.service;

import com.example.moviebox.model.*;
import com.example.moviebox.repository.MovieRepository;
import com.example.moviebox.repository.UserMovieRepository;
import com.example.moviebox.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserMovieService {

    private final UserMovieRepository userMovieRepository;
    private final MovieRepository movieRepository;
    private final UserRepository userRepository;
    private final MovieService movieService;

    public UserMovieService(UserMovieRepository userMovieRepository,
                            MovieRepository movieRepository,
                            UserRepository userRepository,
                            MovieService movieService) {
        this.userMovieRepository = userMovieRepository;
        this.movieRepository = movieRepository;
        this.userRepository = userRepository;
        this.movieService = movieService;
    }

    private User getDefaultUser() {
        return userRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("User par défaut non trouvé"));
    }

    @Transactional
    public UserMovie addToWatchlist(Long tmdbId) {
        User user = getDefaultUser();

        Movie movie = movieRepository.findByTmdbId(tmdbId)
                .orElseGet(() -> movieService.saveMovie(tmdbId));

        Optional<UserMovie> existing = userMovieRepository.findByUserAndMovieId(user, movie.getId());
        if (existing.isPresent()) {
            UserMovie userMovie = existing.get();
            userMovie.setStatus(UserMovieStatus.TO_WATCH);
            userMovie.setRating(null);
            // On remet la date de modification à maintenant pour garder un tri cohérent
            userMovie.setLastModifiedDate(LocalDateTime.now());
            return userMovieRepository.save(userMovie);
        }

        UserMovie userMovie = new UserMovie(user, movie, null);
        return userMovieRepository.save(userMovie);
    }

    public List<UserMovie> getWatchlist() {
        User user = getDefaultUser();
        return userMovieRepository.findByUserAndStatus(user, UserMovieStatus.TO_WATCH);
    }

    @Transactional
    public UserMovie rateMovie(Long tmdbId, Double rating) {
        if (rating < 1 || rating > 10) {
            throw new IllegalArgumentException("La note doit être entre 1 et 10");
        }

        User user = getDefaultUser();

        Movie movie = movieRepository.findByTmdbId(tmdbId)
                .orElseGet(() -> movieService.saveMovie(tmdbId));

        Optional<UserMovie> existing = userMovieRepository.findByUserAndMovieId(user, movie.getId());

        UserMovie userMovie;
        if (existing.isPresent()) {
            userMovie = existing.get();
            userMovie.setStatus(UserMovieStatus.RATED);
            userMovie.setRating(rating);
            userMovie.setLastModifiedDate(LocalDateTime.now());
        } else {
            userMovie = new UserMovie(user, movie, rating);
        }

        return userMovieRepository.save(userMovie);
    }

    public List<UserMovie> getRatedMovies() {
        User user = getDefaultUser();
        return userMovieRepository.findByUserAndStatusOrderByRatingDesc(user, UserMovieStatus.RATED);
    }

    @Transactional
    public void removeFromList(Long tmdbId) {
        User user = getDefaultUser();
        Movie movie = movieRepository.findByTmdbId(tmdbId)
                .orElseThrow(() -> new RuntimeException("Film non trouvé"));

        userMovieRepository.findByUserAndMovieId(user, movie.getId())
                .ifPresent(userMovieRepository::delete);
    }

    public List<UserMovie> getAllUserMovies() {
        User user = getDefaultUser();
        return userMovieRepository.findByUserOrderByLastModifiedDateDesc(user);
    }
}
