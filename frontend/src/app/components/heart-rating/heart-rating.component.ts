import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-heart-rating',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './heart-rating.component.html',
  styleUrl: './heart-rating.component.scss'
})
export class HeartRatingComponent {
  /** Valeur en cœurs (0 à 5, avec pas de 0.5) */
  @Input() value = 0;
  @Input() readonly = false;
  @Input() compact = false;

  @Output() valueChange = new EventEmitter<number>();

  hearts = [1, 2, 3, 4, 5];

  onClick(heartIndex: number): void {
    if (this.readonly) {
      return;
    }

    // Cycle: vide -> demi -> plein -> vide
    const half = heartIndex - 0.5;

    if (this.value < half) {
      this.setValue(half);
    } else if (this.value < heartIndex) {
      this.setValue(heartIndex);
    } else {
      this.setValue(heartIndex - 1 >= 0 ? heartIndex - 1 : 0);
    }
  }

  private setValue(v: number): void {
    // clamp entre 0 et 5
    const clamped = Math.max(0, Math.min(5, v));
    this.value = clamped;
    this.valueChange.emit(clamped);
  }
}


