import { Routes } from '@angular/router';
import { LoginComponent } from './login/login';
import { VistaUsuarioComponent } from './vista-usuario/vista-usuario';

export const routes: Routes = [
  { path: '', component: LoginComponent },
  { path: 'vista-usuario', component: VistaUsuarioComponent },
  { path: '**', redirectTo: '' }
];
