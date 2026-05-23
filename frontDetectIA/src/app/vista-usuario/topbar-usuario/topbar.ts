import { Component, Output, EventEmitter, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UsuarioService } from '../../services/usuario.service';
import { ToastrService } from 'ngx-toastr';

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
  private toastr: ToastrService = inject(ToastrService);


  showProfileMenu = false;

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
        this.user.name = resultado.nombreUsuario;
        this.user.email = resultado.correo;
        this.user.files = resultado.totalArchivos;
      },
      error: (e) => {
        this.toastr.error(e.error ||'No se trajeron los datos', 'Error');
      },
    });
  }
}
