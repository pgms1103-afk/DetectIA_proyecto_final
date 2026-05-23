import { Routes } from '@angular/router';
import { Login } from './login/login';
import { VistaAdmin } from './vista-admin/vista-admin';
import { VistaUsuario } from './vista-usuario/vista-usuario';
import { adminGuard, usuarioGuard } from './guards/role.guard';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'login',
    pathMatch: 'full'
  },
  {
    path: 'login',
    component: Login
  },
  {
    path: 'admin',
    component: VistaAdmin,
    canActivate: [adminGuard]
  },
  {
    path: 'usuario',
    component: VistaUsuario,
    canActivate: [usuarioGuard]
  },
  {

    path: '**',
    redirectTo: 'login'
  }
];
