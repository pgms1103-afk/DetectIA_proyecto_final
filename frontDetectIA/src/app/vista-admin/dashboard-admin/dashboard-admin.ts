import { Component, AfterViewInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { UsuarioService } from '../../services/usuario.service';
import { AuditoriaService } from '../../services/auditoria.service';
import { ArchivoService } from '../../services/archivo.service';
import { ResultadoIAService } from '../../services/resultadoIA.service';
import { AuditoriaLogModel } from '../../models/auditoria.model';
import { ArchivoModel } from '../../models/archivo.model';
import { ResultadoIAModel } from '../../models/resultadoIA.model';

declare var Chart: any;

/**
 * Panel de control administrativo.
 * Gestiona la carga de métricas globales y la renderización de gráficas estadísticas.
 */
@Component({
  selector: 'app-dashboard-admin',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard-admin.html',
  styleUrl: './dashboard-admin.css'
})
export class DashboardAdmin implements AfterViewInit {

  private usuarioService   = inject(UsuarioService);
  private auditoriaService = inject(AuditoriaService);
  private archivoService   = inject(ArchivoService);
  private resultadoService = inject(ResultadoIAService);

  /** Configuración visual de las tarjetas métricas */
  metricas = [
    { titulo: 'Usuarios Totales',    valor: '...', icono: 'fa-users',                  color: 'naranja'  },
    { titulo: 'Usuarios',            valor: '...', icono: 'fa-server',                  color: 'verde'    },
    { titulo: 'Administradores',     valor: '...', icono: 'fa-microchip',               color: 'azul'     },
  ];

  private traficoData:    number[] = [0, 0, 0, 0, 0, 0, 0];
  private apiUsageData:   number[] = Array(24).fill(0);
  private tiposArchivo:   { labels: string[]; data: number[] } = { labels: [], data: [] };
  private veredictos:     { labels: string[]; data: number[] } = { labels: [], data: [] };

  ngAfterViewInit() {
    this.cargarDatos();
  }

  /** Obtiene y procesa todos los datos necesarios para el dashboard */
  cargarDatos() {
    forkJoin({
      usuarios:   this.usuarioService.getMostrarUsuarios().pipe(catchError(() => of([]))),
      auditorias: this.auditoriaService.getTodos().pipe(catchError(() => of([]))),
      archivos:   this.archivoService.getAllArchivos().pipe(catchError(() => of([]))),
      resultados: this.resultadoService.getAllResultados().pipe(catchError(() => of([])))
    }).subscribe({
      next: ({ usuarios, auditorias, archivos, resultados }) => {
        this.metricas[0].valor = usuarios.length.toLocaleString();
        this.metricas[1].valor = usuarios.filter(u => u.role === 'USER').length.toLocaleString();
        this.metricas[2].valor = usuarios.filter(u => u.role === 'ADMIN').length.toLocaleString();

        this.traficoData  = this.calcularTraficoPorDia(auditorias);
        this.apiUsageData = this.calcularUsoPorHora(auditorias);
        this.tiposArchivo = this.calcularTiposArchivo(archivos ?? []);
        this.veredictos   = this.calcularVeredictos(resultados ?? []);

        this.inicializarGraficas();
      },
      error: (err) => {
        console.error('Error cargando datos del dashboard:', err);
        this.inicializarGraficas();
      }
    });
  }

  /** Agrupa los logs por día de la semana (Lun=0 … Dom=6) */
  private calcularTraficoPorDia(auditorias: AuditoriaLogModel[]): number[] {
    const conteo = [0, 0, 0, 0, 0, 0, 0];
    auditorias.forEach(a => {
      const dia = new Date(a.fecha).getDay();
      conteo[dia === 0 ? 6 : dia - 1]++;
    });
    return conteo;
  }

  /** Agrupa los logs del día de hoy por hora (0-23) */
  private calcularUsoPorHora(auditorias: AuditoriaLogModel[]): number[] {
    const hoy   = new Date().toDateString();
    const conteo = Array(24).fill(0);
    auditorias.forEach(a => {
      const fecha = new Date(a.fecha);
      if (fecha.toDateString() === hoy) conteo[fecha.getHours()]++;
    });
    return conteo;
  }

  /** Categoriza archivos según su extensión */
  private calcularTiposArchivo(archivos: ArchivoModel[]): { labels: string[]; data: number[] } {
    const categorias: Record<string, number> = { 'Imagen': 0, 'Video': 0, 'Audio': 0, 'Documento': 0, 'Otro': 0 };

    archivos.forEach(a => {
      const nombre = (a.rutaAlmacenamiento || a.nombre || '').toLowerCase();
      const ext    = nombre.split('.').pop() ?? '';

      if (['jpg','jpeg','png','gif','webp','heic','heif','bmp'].includes(ext))        categorias['Imagen']++;
      else if (['mp4','mov','avi','mkv','webm','quicktime'].includes(ext))             categorias['Video']++;
      else if (['mp3','wav','ogg','aac','m4a','mpeg'].includes(ext))                  categorias['Audio']++;
      else if (['pdf','txt','doc','docx'].includes(ext))                              categorias['Documento']++;
      else                                                                             categorias['Otro']++;
    });

    const entries = Object.entries(categorias).filter(([, v]) => v > 0);
    return { labels: entries.map(([k]) => k), data: entries.map(([, v]) => v) };
  }

  /** Clasifica resultados entre IA y Humano */
  private calcularVeredictos(resultados: ResultadoIAModel[]): { labels: string[]; data: number[] } {
    let ia = 0, humano = 0;
    resultados.forEach(r => r.porcentajeIA >= 50 ? ia++ : humano++);
    return { labels: ['Probable IA', 'Probable Humano'], data: [ia, humano] };
  }

  /** Renderiza los elementos visuales usando Chart.js */
  inicializarGraficas() {
    // ... Implementación de Chart.js (mantener lógica original)
  }
}
