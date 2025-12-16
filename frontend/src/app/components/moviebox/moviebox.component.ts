import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserMoviesService } from '../../services/user-movies.service';
import type { UserMovie } from '../../models/user-movie.model';
import type { Movie } from '../../models/movie.model';
import { MoviesService } from '../../services/movies.service';
import { HeartRatingComponent } from '../heart-rating/heart-rating.component';
import { MovieDetailModalComponent } from '../movie-detail-modal/movie-detail-modal.component';

@Component({
  selector: 'app-moviebox',
  standalone: true,
  imports: [CommonModule, FormsModule, HeartRatingComponent, MovieDetailModalComponent],
  templateUrl: './moviebox.component.html',
  styleUrl: './moviebox.component.scss'
})
export class MovieBoxComponent implements OnInit {
  activeTab: 'discover' | 'profile' = 'discover';
  watchlist = signal<UserMovie[]>([]);
  rated = signal<UserMovie[]>([]);

  // Découverte / recherche de films
  discoverMovies = signal<Movie[]>([]);
  searchQuery = '';
  discoverLoading = signal(false);
  private searchDebounceHandle: number | null = null;

  // Modal de détails
  selectedMovie: Movie | null = null;
  showModal = signal(false);

  loading = signal(false);
  error = signal<string | null>(null);

  constructor(
    private readonly userMoviesService: UserMoviesService,
    private readonly moviesService: MoviesService
  ) {}

  ngOnInit(): void {
    this.refreshAll();
    this.loadPopularMovies();
  }

  refreshAll(): void {
    this.loading.set(true);
    this.error.set(null);

    this.userMoviesService.getWatchlist().subscribe({
      next: (wl) => this.watchlist.set(wl),
      error: (err) => this.error.set('Erreur lors du chargement de la watchlist')
    });

    this.userMoviesService.getRated().subscribe({
      next: (rated) => {
        this.rated.set(rated);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set('Erreur lors du chargement des films notés');
        this.loading.set(false);
      }
    });
  }

  // --- Profil / helpers ---

  setTab(tab: 'discover' | 'profile'): void {
    this.activeTab = tab;
  }

  /** Convertit une note backend (1-10) en nombre de cœurs (0.5-5). */
  toHearts(rating: number | null | undefined): number {
    if (!rating) return 0;
    return rating / 2;
  }

  /** Récupère la note en cœurs pour un tmdbId donné dans la liste rated. */
  getHeartsForTmdb(tmdbId: number): number {
    const found = this.rated().find((u) => u.movie.tmdbId === tmdbId);
    return found?.rating ? this.toHearts(found.rating) : 0;
  }

  /** Vérifie si un film est dans la watchlist. */
  isInWatchlist(tmdbId: number): boolean {
    return this.watchlist().some((u) => u.movie.tmdbId === tmdbId);
  }

  /** Ouvre la modal de détails d'un film. */
  openMovieDetail(movie: Movie): void {
    this.selectedMovie = movie;
    this.showModal.set(true);
  }

  /** Ferme la modal. */
  closeModal(): void {
    this.showModal.set(false);
    this.selectedMovie = null;
  }

  /** Gère l'ajout à la watchlist depuis la modal. */
  onModalAddToWatchlist(tmdbId: number): void {
    this.loading.set(true);
    this.error.set(null);
    this.userMoviesService.addToWatchlist(tmdbId).subscribe({
      next: () => {
        this.refreshAll();
      },
      error: () => {
        this.error.set("Impossible d'ajouter ce film à la watchlist");
        this.loading.set(false);
      }
    });
  }

  /** Gère la notation depuis la modal. */
  onModalRateMovie(data: { tmdbId: number; hearts: number }): void {
    const rating = data.hearts * 2; // backend: 1-10
    if (!rating) {
      return;
    }
    this.loading.set(true);
    this.error.set(null);
    this.userMoviesService.rateMovie(data.tmdbId, rating).subscribe({
      next: () => {
        this.refreshAll();
      },
      error: () => {
        this.error.set('Impossible de noter ce film.');
        this.loading.set(false);
      }
    });
  }

  /** Gère la suppression depuis la modal. */
  onModalRemove(tmdbId: number): void {
    this.loading.set(true);
    this.error.set(null);
    this.userMoviesService.removeFromList(tmdbId).subscribe({
      next: () => {
        this.refreshAll();
      },
      error: () => {
        this.error.set('Impossible de supprimer ce film.');
        this.loading.set(false);
      }
    });
  }

  /** Récupère la note actuelle d'un film pour la modal. */
  getCurrentRatingForMovie(tmdbId: number | null | undefined): number | null {
    if (!tmdbId) return null;
    const found = this.rated().find((u) => u.movie.tmdbId === tmdbId);
    return found?.rating ?? null;
  }


  // --- Découverte / recherche ---

  loadPopularMovies(): void {
    this.discoverLoading.set(true);
    this.moviesService.getPopular().subscribe({
      next: (movies) => {
        this.discoverMovies.set(movies);
        this.discoverLoading.set(false);
      },
      error: () => {
        // On ne casse pas le reste de l UI si la découverte échoue
        this.discoverLoading.set(false);
      }
    });
  }

  onSearchQueryChange(value: string): void {
    this.searchQuery = value;

    // petit debounce pour éviter un appel HTTP à chaque frappe
    if (this.searchDebounceHandle !== null) {
      window.clearTimeout(this.searchDebounceHandle);
    }

    this.searchDebounceHandle = window.setTimeout(() => {
      this.searchMovies();
    }, 400);
  }

  searchMovies(): void {
    const query = this.searchQuery.trim();
    if (!query) {
      this.loadPopularMovies();
      return;
    }

    this.discoverLoading.set(true);
    this.moviesService.search(query).subscribe({
      next: (movies) => {
        this.discoverMovies.set(movies);
        this.discoverLoading.set(false);
      },
      error: () => {
        this.discoverLoading.set(false);
      }
    });
  }


  removeFromList(tmdbId: number): void {
    this.loading.set(true);
    this.error.set(null);
    this.userMoviesService.removeFromList(tmdbId).subscribe({
      next: () => this.refreshAll(),
      error: () => {
        this.error.set('Impossible de supprimer ce film.');
        this.loading.set(false);
      }
    });
  }
}


