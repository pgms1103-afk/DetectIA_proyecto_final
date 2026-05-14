import { Routes } from '@angular/router';
import { Login } from './login/login';
import { VistaUsuario } from './vista-usuario/vista-usuario';
import { VistaAdmin } from './vista-admin/vista-admin';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: Login },
  { path: 'usuario', component: VistaUsuario },
  { path: 'admin', component: VistaAdmin },
];
