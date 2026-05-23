import { Component, Output, EventEmitter, OnInit, inject } from '@angular/core'; // 🟢 Añadimos OnInit e inject
import { CommonModule } from '@angular/common';
import { ArchivoService } from '../../services/archivo.service'; // 🟢 Importamos el servicio
import { ArchivoModel } from '../../models/archivo.model';
import { ResultadoIAService } from '../../services/resultadoIA.service'; // 🟢 Importamos el modelo
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './sidebar.html',
  styleUrl: './sidebar.css',
})
export class Sidebar implements OnInit {
  // 🟢 Implementamos OnInit

  @Output() herramientaSeleccionada = new EventEmitter<string>();

  // 🟢 1. Inyectamos tu servicio de archivos
  private archivoService: ArchivoService = inject(ArchivoService);
  private resultadoService: ResultadoIAService = inject(ResultadoIAService);
  public nombreArchivo = '';

  // 🟢 2. Arreglo para guardar el historial que viene de la BD
  public historialArchivos: ArchivoModel[] = [];
  private authService = inject(AuthService);
  private router = inject(Router);
  private toastr: ToastrService = inject(ToastrService);

  showProfileMenu: boolean = false;
  user = {
    name: 'Jose Manuel',
    email: 'jose.manuel@elbosque.edu.co',
    time: '14h 20m',
    files: 28,
  };

  // 🟢 3. Se ejecuta apenas carga el Sidebar
  ngOnInit(): void {
    this.cargarHistorial();
    this.archivoService.analisisCompletado$.subscribe(() => {
      this.cargarHistorial();
    });
  }

  // 🟢 4. Método que va al backend por los archivos del usuario
  cargarHistorial() {
    this.archivoService.getMisArchivos().subscribe({
      next: (httpResponse) => {
        if (httpResponse.body) {
          this.historialArchivos = httpResponse.body;
          this.toastr.success('Historial cargado en el Sidebar:', 'Exito');
        }
      },
      error: (err) => {
        this.toastr.error(err.error || 'Error al traer el historial:', 'Error');
      },
    });
  }

  // 🟢 5. Método que se dispara al hacer click en un archivo del HTML
  verDetalleHistorial(archivo: ArchivoModel) {
    this.toastr.success('Click en el archivo del historial:', archivo.nombre);

    // 1. Disparamos el evento al Detector a través del "puente"
    this.archivoService.archivoSeleccionado$.next(archivo);

    // 2. Detectamos qué tipo de archivo es según su ruta
    const ruta = archivo.rutaAlmacenamiento || '';
    const extension = ruta.split('.').pop()?.toLowerCase() || '';

    let categoriaDetectada = 'texto'; // Por defecto

    if (['txt', 'pdf', 'docx', 'doc'].includes(extension)) {
      categoriaDetectada = 'texto';
    } else if (['jpg', 'jpeg', 'png', 'gif', 'webp'].includes(extension)) {
      categoriaDetectada = 'imagen';
    } else if (['mp4', 'avi', 'mov', 'mkv'].includes(extension)) {
      categoriaDetectada = 'video';
    } else if (['mp3', 'wav', 'ogg'].includes(extension)) {
      categoriaDetectada = 'audio';
    }

    // 3. Movemos el indicador visual en el Sidebar usando tu propio método
    this.seleccionarHerramienta(categoriaDetectada);
  }
  toggleProfileMenu() {
    this.showProfileMenu = !this.showProfileMenu;
  }

  isCollapsed = false;
  mobileOpen = false;
  herramientaActual = 'texto';

  toggleSidebar() {
    this.isCollapsed = !this.isCollapsed;
  }
  toggleMobileMenu() {
    this.mobileOpen = !this.mobileOpen;
  }

  seleccionarHerramienta(herramienta: string) {
    this.herramientaActual = herramienta;
    this.herramientaSeleccionada.emit(herramienta);
    if (this.mobileOpen) this.toggleMobileMenu();
  }

  cerrarSesion() {
    this.authService.cerrarSesion();
    this.router.navigate(['/login']);
  }

  buscarPorNombre() {
    if (!this.nombreArchivo.trim()) {
      this.cargarHistorial(); // si está vacío, carga todo el historial
      return;
    }

    this.archivoService.getBuscarArchivosPorNombre(this.nombreArchivo).subscribe({
      next: (httpResponse) => {
        if (httpResponse.body) {
          this.historialArchivos = httpResponse.body;
        }
      },
      error: (err) => {
        this.toastr.error(err.error || 'Error al buscar:', 'Error');
        this.historialArchivos = [];
      }
    });
  }
}
