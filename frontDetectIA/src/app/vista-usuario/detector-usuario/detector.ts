import {
  Component,
  inject,
  Input,
  OnInit,
  AfterViewInit,
  OnChanges,
  SimpleChanges,
} from '@angular/core';
import {CommonModule} from '@angular/common';
import { ArchivoService } from '../../services/archivo.service';
import { FormsModule} from '@angular/forms';
import { AnalisisModel } from '../../models/analisis.model';
import { ResultadoIAModel } from '../../models/resultadoIA.model';
import { ResultadoIAService } from '../../services/resultadoIA.service';
import { ArchivoModel } from '../../models/archivo.model';


declare var Chart: any;

interface ModeloIA {
  nombre: string;
  icono: string;
  color: string;
  valor?: number | null;
}

@Component({
  selector: 'app-detector',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './detector.html',
  styleUrl: './detector.css',
})
export class Detector implements OnInit, AfterViewInit, OnChanges {
  private archivoService: ArchivoService = inject(ArchivoService);
  private resultadoService: ResultadoIAService = inject(ResultadoIAService);
  public nombre: string = '';
  public archivo: File | null = null;
  public archivoHistorico: ArchivoModel | null = null;
  public url: string = '';
  public textoPlano: string = '';
  public promedioActual: number = 0;
  public veredictoActual: string = '';

  public cargando: boolean = false;
  public sugerenciaActual: string = '';
  private intervalSugerencias: any;
  public sugerencias: string[] = [
    "Analizando patrones de lenguaje y estructura...",
    "Extrayendo metadatos y firmas ocultas...",
    "Consultando la red neuronal de detección...",
    "Evaluando anomalías en el contenido...",
    "Comparando contra millones de muestras..."
  ];



  get extensionesPermitidas(): string {
    switch (this.tipoHerramienta?.toLowerCase()) {
      case 'texto':
        return '.txt,.pdf,.docx,.doc';
      case 'imagen':
        return '.jpg,.jpeg,.png,.webp';
      case 'video':
        return '.mp4,.mov,.avi';
      case 'audio':
      case 'musica':
        return '.mp3,.wav,.ogg';
      default:
        return '*/*';
    }
  }

  iniciarCarga() {
    this.cargando = true;
    let i = 0;
    this.sugerenciaActual = this.sugerencias[0];

    this.intervalSugerencias = setInterval(() => {
      i = (i + 1) % this.sugerencias.length;
      this.sugerenciaActual = this.sugerencias[i];
    }, 2500);
  }

  detenerCarga() {
    this.cargando = false;
    if (this.intervalSugerencias) {
      clearInterval(this.intervalSugerencias);
    }
  }


  @Input() tipoHerramienta: string = 'texto';

  activeTab: 'text' | 'file' = 'text';
  chartMedidor: any;

  private mapaModelos: Record<string, ModeloIA[]> = {
    texto: [
      { nombre: 'Grok',    icono: 'fa-bolt',          color: '#f59e0b' },
      { nombre: 'Gemini',  icono: 'fa-google',        color: '#3b82f6' },
      { nombre: 'Mistral', icono: 'fa-wind',          color: '#a78bfa' },
      { nombre: 'Winston', icono: 'fa-shield-halved', color: '#10b981' },
    ],
    imagen: [
      { nombre: 'Sightengine',     icono: 'fa-eye',           color: '#f59e0b' },
      { nombre: 'Gemini',          icono: 'fa-google',        color: '#3b82f6' },
      { nombre: 'Hive Moderation', icono: 'fa-shield-halved', color: '#10b981' },
      { nombre: 'Grok',            icono: 'fa-bolt',          color: '#a78bfa' },
    ],
    video: [
      { nombre: 'TwelveLabs',      icono: 'fa-film',          color: '#f59e0b' },
      { nombre: 'Hive Moderation', icono: 'fa-shield-halved', color: '#10b981' },
      { nombre: 'Gemini',          icono: 'fa-google',        color: '#3b82f6' },
    ],
    audio: [
      { nombre: 'ACRCloud', icono: 'fa-music', color: '#f59e0b' },
    ],
    musica: [
      { nombre: 'ACRCloud', icono: 'fa-music', color: '#f59e0b' },
    ],
  };

  modelosActuales: ModeloIA[] = [];


  ngOnInit(): void {
    this.actualizarModelos();

    this.archivoService.archivoSeleccionado$.subscribe((archivoSeleccionado: ArchivoModel) => {

      const categoriaDetectada = this.determinarCategoriaPorArchivo(archivoSeleccionado.rutaAlmacenamiento);
      this.tipoHerramienta = categoriaDetectada;
      this.nombre = archivoSeleccionado.nombre ?? '';

      // Limpia el estado previo
      this.archivo = null;
      this.archivoHistorico = null;
      this.url = '';
      this.textoPlano = '';

      // Detecta si la "rutaAlmacenamiento" es una URL pública o un archivo local
      const esUrl = /^https?:\/\//i.test(archivoSeleccionado.rutaAlmacenamiento ?? '');

      if (esUrl) {
        // Caso URL (imagen/video/audio remoto) → pestaña "Pegar enlace"
        this.activeTab = 'text';
        this.url = archivoSeleccionado.rutaAlmacenamiento;
      } else {
        // Caso archivo local → pestaña "Subir archivo" con tarjeta del archivo del historial
        this.activeTab = 'file';
        this.archivoHistorico = archivoSeleccionado;
      }

      this.resultadoService.getMostrarResultadosPorId(archivoSeleccionado.id).subscribe({
        next: (resultados: ResultadoIAModel[]) => {
          this.actualizarValoresModelos(resultados);
        },
        error: (err) => {

        }
      });

      this.resultadoService.getMostrarAnalisisPorId(archivoSeleccionado.id).subscribe({
        next: (analisisResp: AnalisisModel[]) => {

          if (analisisResp && analisisResp.length > 0) {
            const analisis = analisisResp[0];
            this.actualizarPorcentaje(analisis.porcentajeFinal);
            this.promedioActual = analisis.porcentajeFinal;
            this.veredictoActual = analisis.veredicto;
          }
        },
        error: (err: any) => {
          this.actualizarPorcentaje(0);
          this.promedioActual = 0;
          this.veredictoActual = 'Sin datos';

        }
      });
    });
  }

  quitarArchivoHistorico(event: Event) {
    event.stopPropagation();
    this.archivoHistorico = null;
  }

  ngAfterViewInit(): void {
    // pequeño retraso para asegurar que el canvas esté completamente renderizado
    setTimeout(() => this.inicializarGraficaVacia(), 0);
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['tipoHerramienta']) {
      this.actualizarModelos();

      const herramienta = this.tipoHerramienta?.toLowerCase();
      if (herramienta === 'video' || herramienta === 'audio' || herramienta === 'musica') {
        this.activeTab = 'file';
      }
    }
  }

  private actualizarModelos(): void {
    const clave = this.tipoHerramienta?.toLowerCase() ?? 'texto';
    this.modelosActuales = this.mapaModelos[clave] ?? this.mapaModelos['texto'];
  }

  switchTab(tab: 'text' | 'file') {
    this.activeTab = tab;
  }


  inicializarGraficaVacia() {
    const ctx = document.getElementById('iaProbabilityChart');
    if (ctx) {
      this.chartMedidor = new Chart(ctx, {
        type: 'doughnut',
        data: {
          labels: ['IA', 'Restante'],
          datasets: [{
            data: [0, 100],
            backgroundColor: [
              '#f59e0b',
              '#27272a'
            ],
            borderWidth: 0,
            borderRadius: 20
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          circumference: 180,
          rotation: 270,
          cutout: '85%',
          plugins: {
            legend: { display: false },
            tooltip: { enabled: false }
          },
          animation: {
            animateRotate: true,
            animateScale: false
          }
        }
      });
    }
  }

  actualizarPorcentaje(nuevoPorcentaje: number) {
    // Si la gráfica no existe todavía, la creamos primero y reintentamos
    if (!this.chartMedidor) {
      this.inicializarGraficaVacia();
      setTimeout(() => {
        if (this.chartMedidor) {
          this.chartMedidor.data.datasets[0].data = [nuevoPorcentaje, 100 - nuevoPorcentaje];
          this.chartMedidor.update();
        }
      }, 50);
      return;
    }

    this.chartMedidor.data.datasets[0].data = [nuevoPorcentaje, 100 - nuevoPorcentaje];
    this.chartMedidor.update();
  }

  /** Destruye y recrea la gráfica del medidor manteniendo el valor actual. */
  refrescarGrafica() {
    if (this.chartMedidor) {
      this.chartMedidor.destroy();
      this.chartMedidor = null;
    }
    setTimeout(() => {
      this.inicializarGraficaVacia();
      if (this.promedioActual > 0) {
        this.actualizarPorcentaje(this.promedioActual);
      }
    }, 50);
  }

  subirArchivoLocal() {
    if (!this.archivo) {
      alert('Advertencia: Selecciona un archivo primero');
      return;
    }

    this.iniciarCarga();

    this.archivoService.postAnalizarArchivo(this.nombre, this.archivo).subscribe({
      next: (resp: any) => {
        setTimeout(() => {
          this.detenerCarga();
          this.archivoService.analisisCompletado$.next();

          const valor = resp.promedio ?? resp.porcentajeFinal ?? 0;
          this.actualizarPorcentaje(valor);
          this.promedioActual = valor;
          this.veredictoActual = resp.veredicto;
          this.actualizarValoresModelos(resp.resultados);

        }, 3000);
      },
      error: (err) => {
        setTimeout(() => {
          this.detenerCarga();
          alert('Error: ' + (err.error || 'Error al analizar el archivo.'));
        }, 1500);
      },
    });
  }

  subirArchivoUrl() {
    if (!this.url) {
      alert('Advertencia: Ingresa una URL primero');
      return;
    }

    this.iniciarCarga();

    this.archivoService.postAnalizarUrl(this.nombre, this.url).subscribe({
      next: (resp: any) => {
        setTimeout(() => {
          this.detenerCarga();
          this.archivoService.analisisCompletado$.next();

          // El backend devuelve "promedio" en el Map de calcularResumen
          const valor = resp.promedio ?? resp.porcentajeFinal ?? 0;
          this.actualizarPorcentaje(valor);
          this.promedioActual = valor;
          this.veredictoActual = resp.veredicto;

          if (resp.resultados) {
            this.actualizarValoresModelos(resp.resultados);
          }
        }, 3000);
      },
      error: (err) => {
        setTimeout(() => {
          this.detenerCarga();
          alert('Error: ' + (err.error || 'Error al analizar la URL.'));
        }, 1500);
      },
    });
  }

  onFileChange(event: any) {
    this.archivo = event.target.files[0];
  }

  quitarArchivo(event: Event) {
    event.stopPropagation();
    this.archivo = null;
  }

  ejecutarAnalisis() {

    if (this.activeTab === 'file') {
      this.subirArchivoLocal();

    } else if (this.activeTab === 'text' && this.tipoHerramienta !== 'texto') {
      this.subirArchivoUrl();

    } else {
      // Pestaña "Pegar texto" + tipoHerramienta === 'texto'
      this.subirTextoPlano();
    }
  }

  subirTextoPlano() {
    if (!this.textoPlano || this.textoPlano.trim().length === 0) {
      alert('Advertencia: Pega un texto primero');
      return;
    }

    this.iniciarCarga();

    this.archivoService.postAnalizarTexto(this.nombre, this.textoPlano).subscribe({
      next: (resp: any) => {
        setTimeout(() => {
          this.detenerCarga();
          this.archivoService.analisisCompletado$.next();

          // El backend devuelve "promedio" en el Map de calcularResumen
          const valor = resp.promedio ?? resp.porcentajeFinal ?? 0;
          this.actualizarPorcentaje(valor);
          this.promedioActual = valor;
          this.veredictoActual = resp.veredicto;

          if (resp.resultados) {
            this.actualizarValoresModelos(resp.resultados);
          }
        }, 3000);
      },
      error: (err) => {
        setTimeout(() => {
          this.detenerCarga();
          alert('Error: ' + (err.error || 'Error al analizar el texto.'));
        }, 1500);
      },
    });
  }

  actualizarValoresModelos(datosBackend: any) {
    let datosNormalizados: { nombre: string, valor: number }[] = [];

    if (Array.isArray(datosBackend)) {
      datosNormalizados = datosBackend.map(item => ({
        nombre: item.nombreIA,
        valor: item.porcentajeIA
      }));
    } else {
      datosNormalizados = Object.keys(datosBackend).map(key => ({
        nombre: key,
        valor: datosBackend[key]
      }));
    }

    const todosLosModelosBase = Object.values(this.mapaModelos).flat();


    this.modelosActuales = datosNormalizados.map(dato => {

      const modeloOriginal = todosLosModelosBase.find(
        m => m.nombre.toLowerCase() === dato.nombre.toLowerCase()
      );

      if (modeloOriginal) {
        return {
          ...modeloOriginal,
          valor: dato.valor
        };
      }

      return {
        nombre: dato.nombre,
        icono: 'fa-microchip',
        color: '#888888',
        valor: dato.valor
      };
    });
  }
  determinarCategoriaPorArchivo(ruta: string): string {
    if (!ruta) return 'texto';
    const extension = ruta.split('.').pop()?.toLowerCase() || '';

    if (['txt', 'pdf', 'docx', 'doc'].includes(extension)) {
      return 'texto';
    } else if (['jpg', 'jpeg', 'png', 'gif', 'webp'].includes(extension)) {
      return 'imagen';
    } else if (['mp4', 'avi', 'mov', 'mkv'].includes(extension)) {
      return 'video';
    } else if (['mp3', 'wav', 'ogg'].includes(extension)) {
      return 'audio';
    }

    return 'texto';
  }
}
