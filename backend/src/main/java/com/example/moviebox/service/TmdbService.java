package com.example.moviebox.service;

import com.example.moviebox.dto.MovieDto;
import com.example.moviebox.dto.MovieResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class TmdbService {

    private final RestTemplate restTemplate;

    @Value("${tmdb.api.key}")
    private String apiKey;

    @Value("${tmdb.base.url}")
    private String baseUrl;

    public MovieResponseDto getPopularMovies() {
        String url = buildUrl("/movie/popular");
        return restTemplate.getForObject(url, MovieResponseDto.class);
    }

    public MovieResponseDto searchMovies(String query) {
        String url = UriComponentsBuilder.fromUriString(baseUrl + "/search/movie")
                .queryParam("api_key", apiKey)
                .queryParam("language", "fr-FR")
                .queryParam("query", query)
                .toUriString();

        return restTemplate.getForObject(url, MovieResponseDto.class);
    }

    public MovieDto getMovieDetails(Long id) {
        String url = buildUrl("/movie/" + id);
        return restTemplate.getForObject(url, MovieDto.class);
    }

    private String buildUrl(String endpoint) {
        return UriComponentsBuilder.fromUriString(baseUrl + endpoint)
                .queryParam("api_key", apiKey)
                .queryParam("language", "fr-FR")
                .toUriString();
    }
}
