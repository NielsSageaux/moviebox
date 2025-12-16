package com.example.moviebox.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_movies")
public class UserMovie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "userMovies"})
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "movie_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "userMovies"})
    private Movie movie;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private UserMovieStatus status;

    @Column(name = "rating")
    private Double rating; // Note de 1 à 10

    @Column(name = "last_modified_date")
    private LocalDateTime lastModifiedDate;

    public UserMovie() {
    }

    public UserMovie(User user, Movie movie, Double rating) {
        this.user = user;
        this.movie = movie;
        this.lastModifiedDate = LocalDateTime.now();
        if (rating == null) {
            this.status = UserMovieStatus.TO_WATCH;
        } else {
            this.rating = rating;
            this.status = UserMovieStatus.RATED;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public UserMovieStatus getStatus() {
        return status;
    }

    public void setStatus(UserMovieStatus status) {
        this.status = status;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public LocalDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
}
