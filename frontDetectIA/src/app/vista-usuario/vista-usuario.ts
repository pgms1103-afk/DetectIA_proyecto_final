
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Sidebar } from './sidebar/sidebar';
import { Topbar} from './topbar/topbar';
import { Detector } from './detector/detector';

@Component({
  selector: 'app-vista-usuario',
  standalone: true,
  imports: [
    CommonModule,
    Sidebar,
    Topbar,
    Detector,
  ],
  templateUrl: './vista-usuario.html',
  styleUrls: ['./vista-usuario.css']
})
export class VistaUsuarioComponent {

}
