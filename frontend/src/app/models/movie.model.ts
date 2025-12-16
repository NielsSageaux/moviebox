export interface Movie {
  id: number;
  tmdbId: number;
  title: string;
  releaseYear?: number | null;
  director?: string | null;
  synopsis?: string | null;
  posterUrl?: string | null;
}


