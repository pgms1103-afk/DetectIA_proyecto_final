import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';


export const adminGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);
  const rol = localStorage.getItem('rol_diario');


  if (rol === 'ADMIN' || rol === 'ROLE_ADMIN') {
    return true;
  }

  router.navigate(['/login']);
  return false;
};

export const usuarioGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);
  const rol = localStorage.getItem('rol_diario');


  if (rol === 'USER' || rol === 'ROLE_USER' || rol === 'USUARIO') {
    return true;
  }

  router.navigate(['/login']);
  return false;
};
