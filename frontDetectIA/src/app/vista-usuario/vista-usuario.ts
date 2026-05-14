import { Component } from '@angular/core';
import { Sidebar } from './sidebar-usuario/sidebar';
import { Topbar } from './topbar-usuario/topbar';
import { Detector } from './detector-usuario/detector';

@Component({
  selector: 'app-vista-usuario',
  standalone: true,
  imports: [Sidebar, Topbar, Detector],
  templateUrl: './vista-usuario.html',
  styleUrl: './vista-usuario.css'
})
export class VistaUsuario {
  herramientaActual = 'texto';

  cambiarHerramienta(herramienta: string) {
    this.herramientaActual = herramienta;
  }
}
