// src/app/app.routes.ts
import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '', // The default route
    // Lazy-load the component
    loadComponent: () => import('./vista-usuario/vista-usuario').then(m => m.VistaUsuarioComponent)
  },
  // Optionally add other routes
  // { path: 'dashboard', component: DashboardComponent },
  { path: '**', redirectTo: '' } // Wildcard route to redirect to default
];
