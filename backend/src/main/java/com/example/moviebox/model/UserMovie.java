package com.example.moviebox.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Optional;

@Setter
@Getter
@Entity
@Builder
@Table(name = "user_movies")
@NoArgsConstructor
@AllArgsConstructor
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
}
