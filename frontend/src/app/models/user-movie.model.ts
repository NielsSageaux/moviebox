import type { Movie } from './movie.model';

export type UserMovieStatus = 'TO_WATCH' | 'RATED';

export interface UserMovie {
  id: number;
  user: {
    id: number;
    username: string;
    email: string;
  };
  movie: Movie;
  status: UserMovieStatus;
  rating?: number | null;
  lastModifiedDate?: string | null;
}


