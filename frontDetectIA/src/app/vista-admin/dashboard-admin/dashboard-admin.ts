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

        // --- Datos para gráficas de tiempo ---
        this.traficoData  = this.calcularTraficoPorDia(auditorias);
        this.apiUsageData = this.calcularUsoPorHora(auditorias);

        // --- Datos para gráficas nuevas ---
        this.tiposArchivo = this.calcularTiposArchivo(archivos   ?? []);
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

  /** Agrupa archivos por categoría según la extensión del nombre */
  private calcularTiposArchivo(archivos: ArchivoModel[]): { labels: string[]; data: number[] } {
    const categorias: Record<string, number> = {
      'Imagen':    0,
      'Video':     0,
      'Audio':     0,
      'Documento': 0,
      'Otro':      0
    };

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
    return {
      labels: entries.map(([k]) => k),
      data:   entries.map(([, v]) => v)
    };
  }

  /** Clasifica resultados como PROBABLE IA o PROBABLE HUMANO */
  private calcularVeredictos(resultados: ResultadoIAModel[]): { labels: string[]; data: number[] } {
    let ia      = 0;
    let humano  = 0;
    resultados.forEach(r => r.porcentajeIA >= 50 ? ia++ : humano++);
    return {
      labels: ['Probable IA', 'Probable Humano'],
      data:   [ia, humano]
    };
  }

  inicializarGraficas() {

    // --- Tráfico Global de Análisis ---
    const ctxTrafico = document.getElementById('traficoChart');
    if (ctxTrafico) {
      new Chart(ctxTrafico, {
        type: 'line',
        data: {
          labels: ['Lun', 'Mar', 'Mié', 'Jue', 'Vie', 'Sáb', 'Dom'],
          datasets: [{
            label: 'Peticiones API',
            data: this.traficoData,
            borderColor: '#6366f1',
            backgroundColor: 'rgba(99, 102, 241, 0.1)',
            fill: true,
            tension: 0.4
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: { legend: { display: false } },
          scales: {
            y: { grid: { color: 'rgba(255,255,255,0.05)' }, ticks: { color: '#a1a1aa' } },
            x: { grid: { display: false }, ticks: { color: '#a1a1aa' } }
          }
        }
      });
    }

    // --- Archivos por Tipo (reemplaza Almacenamiento) ---
    const ctxStorage = document.getElementById('storageChart');
    if (ctxStorage) {
      new Chart(ctxStorage, {
        type: 'doughnut',
        data: {
          labels: this.tiposArchivo.labels,
          datasets: [{
            data: this.tiposArchivo.data,
            backgroundColor: ['#6366f1', '#10b981', '#fbbf24', '#f43f5e', '#64748b'],
            borderWidth: 0
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          cutout: '70%',
          plugins: {
            legend: { display: true, position: 'bottom', labels: { color: '#a1a1aa', font: { size: 11 } } }
          }
        }
      });
    }

    // --- Veredictos de Análisis (reemplaza Radar de Modelos) ---
    const ctxRadar = document.getElementById('performanceRadarChart');
    if (ctxRadar) {
      new Chart(ctxRadar, {
        type: 'doughnut',
        data: {
          labels: this.veredictos.labels,
          datasets: [{
            data: this.veredictos.data,
            backgroundColor: ['#f43f5e', '#10b981'],
            borderWidth: 0
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          cutout: '70%',
          plugins: {
            legend: { display: true, position: 'bottom', labels: { color: '#a1a1aa', font: { size: 11 } } }
          }
        }
      });
    }

    // --- Uso de API (Últimas 24 Horas) ---
    const ctxApiUsage = document.getElementById('apiUsageChart');
    if (ctxApiUsage) {
      new Chart(ctxApiUsage, {
        type: 'line',
        data: {
          labels: Array.from({ length: 24 }, (_, i) => `${i}h`),
          datasets: [{
            data: this.apiUsageData,
            borderColor: '#fbbf24',
            backgroundColor: 'rgba(251, 191, 36, 0.1)',
            fill: true,
            tension: 0.1
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: { legend: { display: false } },
          scales: {
            y: { grid: { color: 'rgba(255,255,255,0.05)' }, ticks: { color: '#a1a1aa' } },
            x: { grid: { display: false }, ticks: { color: '#a1a1aa' }, display: false }
          }
        }
      });
    }
  }
}
