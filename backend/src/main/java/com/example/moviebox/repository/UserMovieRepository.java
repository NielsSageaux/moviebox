package com.example.moviebox.repository;

import com.example.moviebox.model.User;
import com.example.moviebox.model.UserMovie;
import com.example.moviebox.model.UserMovieStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserMovieRepository extends JpaRepository<UserMovie, Long> {

    Optional<UserMovie> findByUserAndMovieId(User user, Long movieId);

    List<UserMovie> findByUserAndStatus(User user, UserMovieStatus status);

    List<UserMovie> findByUserOrderByLastModifiedDateDesc(User user);

    List<UserMovie> findByUserAndStatusOrderByRatingDesc(User user, UserMovieStatus status);
}
