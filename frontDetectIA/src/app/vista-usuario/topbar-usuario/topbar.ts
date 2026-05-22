import { Component, Output, EventEmitter, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UsuarioService } from '../../services/usuario.service';

@Component({
  selector: 'app-topbar',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './topbar.html',
  styleUrl: './topbar.css',
})
export class Topbar implements OnInit {

  ngOnInit(): void {
    this.mostrarMisDatos();
  }
  @Output() menuToggle = new EventEmitter<void>();
  usuarioService: UsuarioService = inject(UsuarioService);

  showProfileMenu: boolean = false;

  user = {
    name: 'cargando...',
    email: 'cargando...',
    files: 0,
  };

  abrirMenu() {
    this.menuToggle.emit();
  }

  toggleProfileMenu() {
    this.showProfileMenu = !this.showProfileMenu;
  }

  mostrarMisDatos() {
    this.usuarioService.getDatosUsuarioRegistrado().subscribe({
      next: (resultado) => {
        console.log('Datos traidos');
        this.user.name = resultado.nombreUsuario;
        this.user.email = resultado.correo;
        this.user.files = resultado.totalArchivos;
      },
      error: (error) => {
        console.error('No se trajeron los datos');
      },
    });
  }
}
