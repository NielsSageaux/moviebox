import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import type { UserMovie } from '../models/user-movie.model';

const API_BASE_URL = 'http://localhost:8080/api/user-movies';

@Injectable({
  providedIn: 'root'
})
export class UserMoviesService {
  private readonly http = inject(HttpClient);

  getWatchlist(): Observable<UserMovie[]> {
    return this.http.get<UserMovie[]>(`${API_BASE_URL}/watchlist`);
  }

  getRated(): Observable<UserMovie[]> {
    return this.http.get<UserMovie[]>(`${API_BASE_URL}/rated`);
  }

  addToWatchlist(tmdbId: number): Observable<UserMovie> {
    return this.http.post<UserMovie>(`${API_BASE_URL}/watchlist/${tmdbId}`, {});
  }

  rateMovie(tmdbId: number, rating: number): Observable<UserMovie> {
    return this.http.post<UserMovie>(`${API_BASE_URL}/rate/${tmdbId}`, {
      rating
    });
  }

  removeFromList(tmdbId: number): Observable<void> {
    return this.http.delete<void>(`${API_BASE_URL}/${tmdbId}`);
  }
}


