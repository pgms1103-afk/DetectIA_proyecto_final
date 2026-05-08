import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./login/login').then(m => m.LoginComponent)
  },
  {
    path: 'detector',
    loadComponent: () => import('./vista-usuario/vista-usuario').then(m => m.VistaUsuarioComponent)
  },
  { path: '**', redirectTo: '' }
];
