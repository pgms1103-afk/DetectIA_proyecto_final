import { Component, Output, EventEmitter, OnInit, inject } from '@angular/core';
import { CommonModule} from '@angular/common';
import { UsuarioService } from '../../services/usuario.service';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-topbar-admin',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './topbar-admin.html',
  styleUrl: './topbar-admin.css',
})
export class TopbarAdmin implements OnInit {
  usuarioService: UsuarioService = inject(UsuarioService);
  private toastr: ToastrService = inject(ToastrService);
  ngOnInit(): void {
    this.mostrarMisDatos();
  }

  @Output() menuToggle = new EventEmitter<void>();

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
        this.toastr.error(e.error || 'No se trajeron los datos', 'Error');
      },
    });
  }
}
