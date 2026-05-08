import { Component } from '@angular/core';
import { Sidebar } from './sidebar/sidebar';
import { Topbar } from './topbar/topbar';
import { Detector } from './detector/detector';

@Component({
  selector: 'app-vista-usuario',
  standalone: true,
  imports: [Sidebar, Topbar, Detector], // Aquí conectamos todo directo
  templateUrl: './vista-usuario.html',
  styleUrls: ['./vista-usuario.css']
})
export class VistaUsuarioComponent {}
