import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import type { Movie } from '../models/movie.model';

interface MovieSearchResponse {
  results: Array<{
    id: number;
    title: string;
    overview?: string | null;
    poster_path?: string | null;
    release_date?: string | null;
  }>;
}

const API_BASE_URL = 'http://localhost:8080/api/movies';

@Injectable({
  providedIn: 'root'
})
export class MoviesService {
  private readonly http = inject(HttpClient);

  /**
   * Films populaires depuis TMDB via le backend.
   */
  getPopular(): Observable<Movie[]> {
    return this.http.get<MovieSearchResponse>(`${API_BASE_URL}/popular`).pipe(
      // On adapte la réponse TMDB au modèle Movie du front
      // eslint-disable-next-line rxjs/no-explicit-generics
      mapToMovies()
    );
  }

  /**
   * Recherche de films par nom (via TMDB, filtrée côté backend).
   */
  search(query: string): Observable<Movie[]> {
    return this.http
      .get<MovieSearchResponse>(`${API_BASE_URL}/search`, {
        params: { query }
      })
      .pipe(
        // eslint-disable-next-line rxjs/no-explicit-generics
        mapToMovies()
      );
  }
}

/**
 * Opérateur helper pour convertir la réponse MovieSearchResponse en Movie[].
 */
import { map } from 'rxjs/operators';

function mapToMovies() {
  return map((response: MovieSearchResponse): Movie[] => {
    if (!response?.results) {
      return [];
    }

    return response.results.map((dto) => {
      const year =
        dto.release_date && dto.release_date.length >= 4
          ? Number.parseInt(dto.release_date.substring(0, 4), 10)
          : null;

      return {
        id: dto.id,
        tmdbId: dto.id,
        title: dto.title,
        releaseYear: Number.isNaN(year) ? null : year,
        director: null,
        synopsis: dto.overview ?? null,
        posterUrl: dto.poster_path ? `https://image.tmdb.org/t/p/w300${dto.poster_path}` : null
      } satisfies Movie;
    });
  });
}


