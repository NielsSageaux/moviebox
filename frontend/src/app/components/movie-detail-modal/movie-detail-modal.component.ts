import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import type { Movie } from '../../models/movie.model';
import { HeartRatingComponent } from '../heart-rating/heart-rating.component';

@Component({
  selector: 'app-movie-detail-modal',
  standalone: true,
  imports: [CommonModule, HeartRatingComponent],
  templateUrl: './movie-detail-modal.component.html',
  styleUrl: './movie-detail-modal.component.scss'
})
export class MovieDetailModalComponent {
  @Input() movie: Movie | null = null;
  @Input() isInWatchlist = false;
  @Input() currentRating: number | null = null;
  @Output() close = new EventEmitter<void>();
  @Output() addToWatchlist = new EventEmitter<number>();
  @Output() rateMovie = new EventEmitter<{ tmdbId: number; hearts: number }>();
  @Output() removeFromList = new EventEmitter<number>();

  onClose(): void {
    this.close.emit();
  }

  onAddToWatchlist(): void {
    if (this.movie) {
      this.addToWatchlist.emit(this.movie.tmdbId);
    }
  }

  onRate(hearts: number): void {
    if (this.movie) {
      this.rateMovie.emit({ tmdbId: this.movie.tmdbId, hearts });
    }
  }

  onRemove(): void {
    if (this.movie) {
      this.removeFromList.emit(this.movie.tmdbId);
    }
  }

  toHearts(rating: number | null | undefined): number {
    if (!rating) return 0;
    return rating / 2;
  }
}

