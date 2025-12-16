import { Routes } from '@angular/router';
import { MovieBoxComponent } from './components/moviebox/moviebox.component';

export const routes: Routes = [
  {
    path: '',
    component: MovieBoxComponent
  },
  {
    path: '**',
    redirectTo: ''
  }
];
