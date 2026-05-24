import {
  Component,
  Output,
  EventEmitter,
  OnInit,
  inject
} from '@angular/core';

import { CommonModule } from '@angular/common';
import { ArchivoService } from '../../services/archivo.service';
import { ArchivoModel } from '../../models/archivo.model';
import { ResultadoIAService } from '../../services/resultadoIA.service';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ToastrService } from 'ngx-toastr';

/**
 * Componente Sidebar encargado de:
 * - Mostrar el historial de archivos.
 * - Cambiar entre herramientas.
 * - Gestionar búsqueda, edición y eliminación.
 * - Manejar navegación y autenticación.
 */
@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './sidebar.html',
  styleUrl: './sidebar.css',
})
export class Sidebar implements OnInit {

  /**
   * Evento emitido al seleccionar una herramienta.
   */
  @Output()
  herramientaSeleccionada = new EventEmitter<string>();

  /**
   * Servicio encargado del manejo de archivos.
   */
  private archivoService: ArchivoService = inject(ArchivoService);

  /**
   * Servicio encargado de resultados IA.
   */
  private resultadoService: ResultadoIAService = inject(ResultadoIAService);

  /**
   * Servicio de autenticación.
   */
  private authService = inject(AuthService);

  /**
   * Servicio de navegación.
   */
  private router = inject(Router);

  /**
   * Servicio de notificaciones.
   */
  private toastr: ToastrService = inject(ToastrService);

  /**
   * Nombre usado en el buscador.
   */
  public nombreArchivo = '';

  /**
   * Controla la visibilidad del modal de edición.
   */
  public mostrarModalEditar = false;

  /**
   * Nombre temporal usado al editar.
   */
  public nombreEditando = '';

  /**
   * Archivo actualmente en edición.
   */
  private archivoEditando: ArchivoModel | null = null;

  /**
   * Historial de archivos obtenido del backend.
   */
  public historialArchivos: ArchivoModel[] = [];

  /**
   * Controla la visibilidad del menú de perfil.
   */
  showProfileMenu: boolean = false;

  /**
   * Información básica del usuario.
   */
  user = {
    name: 'Jose Manuel',
    email: 'jose.manuel@elbosque.edu.co',
    time: '14h 20m',
    files: 28,
  };

  /**
   * Estado de colapso del sidebar.
   */
  isCollapsed = false;

  /**
   * Estado del menú móvil.
   */
  mobileOpen = false;

  /**
   * Herramienta actualmente seleccionada.
   */
  herramientaActual = 'texto';

  /**
   * Indica si el usuario es administrador.
   */
  esAdmin = ['ADMIN', 'ROLE_ADMIN']
    .includes(localStorage.getItem('rol_diario') ?? '');

  /**
   * Inicializa el componente.
   */
  ngOnInit(): void {

    this.cargarHistorial();

    this.archivoService.analisisCompletado$.subscribe(() => {
      this.cargarHistorial();
    });
  }

  /**
   * Carga el historial de archivos desde el backend.
   */
  cargarHistorial() {

    this.archivoService.getMisArchivos().subscribe({
      next: (httpResponse) => {

        if (httpResponse.body) {
          this.historialArchivos = httpResponse.body;
        }
      }
    });
  }

  /**
   * Muestra el detalle de un archivo del historial.
   */
  verDetalleHistorial(archivo: ArchivoModel) {

    /**
     * Envía el archivo seleccionado al Detector.
     */
    this.archivoService.archivoSeleccionado$.next(archivo);

    /**
     * Obtiene la extensión del archivo.
     */
    const ruta = archivo.rutaAlmacenamiento || '';

    const extension =
      ruta.split('.').pop()?.toLowerCase() || '';

    let categoriaDetectada = 'texto';

    if (['txt', 'pdf', 'docx', 'doc'].includes(extension)) {

      categoriaDetectada = 'texto';

    } else if (
      ['jpg', 'jpeg', 'png', 'gif', 'webp']
        .includes(extension)
    ) {

      categoriaDetectada = 'imagen';

    } else if (
      ['mp4', 'avi', 'mov', 'mkv']
        .includes(extension)
    ) {

      categoriaDetectada = 'video';

    } else if (
      ['mp3', 'wav', 'ogg']
        .includes(extension)
    ) {

      categoriaDetectada = 'audio';
    }

    /**
     * Actualiza visualmente la herramienta seleccionada.
     */
    this.seleccionarHerramienta(categoriaDetectada);
  }

  /**
   * Muestra u oculta el menú de perfil.
   */
  toggleProfileMenu() {
    this.showProfileMenu = !this.showProfileMenu;
  }

  /**
   * Alterna el estado colapsado del sidebar.
   */
  toggleSidebar() {
    this.isCollapsed = !this.isCollapsed;
  }

  /**
   * Abre o cierra el menú móvil.
   */
  toggleMobileMenu() {
    this.mobileOpen = !this.mobileOpen;
  }

  /**
   * Cambia la herramienta seleccionada.
   */
  seleccionarHerramienta(herramienta: string) {

    this.herramientaActual = herramienta;

    this.herramientaSeleccionada.emit(herramienta);

    if (this.mobileOpen) {
      this.toggleMobileMenu();
    }
  }

  /**
   * Navega hacia el panel de administración.
   */
  irAlAdmin() {
    this.router.navigate(['/admin']);
  }

  /**
   * Cierra la sesión actual.
   */
  cerrarSesion() {

    this.authService.cerrarSesion();

    this.router.navigate(['/login']);
  }

  /**
   * Busca archivos por nombre.
   */
  buscarPorNombre() {

    if (!this.nombreArchivo.trim()) {
      this.cargarHistorial();
      return;
    }

    this.archivoService
      .getBuscarArchivosPorNombre(this.nombreArchivo)
      .subscribe({

        next: (httpResponse) => {

          if (httpResponse.body) {
            this.historialArchivos = httpResponse.body;
          }
        },

        error: (err) => {

          this.toastr.error(
            err.error || 'Error al buscar:',
            'Error'
          );

          this.historialArchivos = [];
        }
      });
  }

  /**
   * Abre el modal para editar un archivo.
   */
  abrirModalEditar(archivo: ArchivoModel) {

    this.archivoEditando = archivo;

    this.nombreEditando = archivo.nombre;

    this.mostrarModalEditar = true;
  }

  /**
   * Cierra el modal de edición.
   */
  cerrarModalEditar() {

    this.mostrarModalEditar = false;

    this.archivoEditando = null;

    this.nombreEditando = '';
  }

  /**
   * Guarda el nuevo nombre del archivo.
   */
  guardarNombre() {

    if (
      !this.archivoEditando ||
      !this.nombreEditando.trim()
    ) return;

    this.archivoService
      .putEditarNombre(
        this.archivoEditando.id,
        this.nombreEditando
      )
      .subscribe({

        next: () => {

          this.toastr.success(
            'Nombre actualizado correctamente',
            'Exito'
          );

          this.cerrarModalEditar();

          this.cargarHistorial();
        },

        error: (err) => {

          this.toastr.error(
            err.error || 'Error al editar:',
            'Error'
          );
        }
      });
  }

  /**
   * Elimina un archivo del historial.
   */
  eliminarArchivo(archivo: ArchivoModel) {

    this.archivoService
      .deleteArchivos(archivo.id)
      .subscribe({

        next: () => {

          this.toastr.success(
            'Archivo eliminado correctamente',
            'Exito'
          );

          this.cargarHistorial();
        },

        error: (err) => {

          this.toastr.error(
            err.error || 'Error al eliminar:',
            'Error'
          );
        }
      });
  }
}
