import {
  Component,
  inject,
  Input,
  OnInit,
  AfterViewInit,
  OnChanges,
  SimpleChanges,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { ArchivoService } from '../../services/archivo.service';
import { FormsModule } from '@angular/forms';
import { AnalisisModel } from '../../models/analisis.model';
import { ResultadoIAModel } from '../../models/resultadoIA.model';
import { ResultadoIAService } from '../../services/resultadoIA.service';
import { ArchivoModel } from '../../models/archivo.model';
import { ToastrService } from 'ngx-toastr';
import { HttpErrorResponse } from '@angular/common/http';

declare let Chart: any;

/**
 * Representa un modelo de IA mostrado en la interfaz.
 */
interface ModeloIA {

  /**
   * Nombre del modelo de IA.
   */
  nombre: string;

  /**
   * Icono FontAwesome asociado al modelo.
   */
  icono: string;

  /**
   * Color principal usado en la interfaz.
   */
  color: string;

  /**
   * Valor de probabilidad generado por la IA.
   */
  valor?: number | null;
}

/**
 * Componente principal encargado de:
 * - Analizar texto, archivos o URLs.
 * - Mostrar resultados de detección IA.
 * - Administrar gráficas y modelos.
 * - Gestionar archivos del historial.
 */
@Component({
  selector: 'app-detector',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './detector.html',
  styleUrl: './detector.css',
})
export class Detector implements OnInit, AfterViewInit, OnChanges {

  /**
   * Servicio encargado del manejo de archivos y análisis.
   */
  private archivoService: ArchivoService = inject(ArchivoService);

  /**
   * Servicio encargado de obtener resultados IA.
   */
  private resultadoService: ResultadoIAService = inject(ResultadoIAService);

  /**
   * Servicio de notificaciones.
   */
  private toastr: ToastrService = inject(ToastrService);

  /**
   * Nombre del análisis actual.
   */
  public nombre: string = '';

  /**
   * Archivo local seleccionado.
   */
  public archivo: File | null = null;

  /**
   * Archivo cargado desde el historial.
   */
  public archivoHistorico: ArchivoModel | null = null;

  /**
   * URL ingresada por el usuario.
   */
  public url: string = '';

  /**
   * Texto plano ingresado para analizar.
   */
  public textoPlano: string = '';

  /**
   * Promedio final de detección IA.
   */
  public promedioActual: number = 0;

  /**
   * Veredicto generado por el backend.
   */
  public veredictoActual: string = '';

  /**
   * Indica si el sistema está procesando un análisis.
   */
  public cargando: boolean = false;

  /**
   * Sugerencia mostrada durante la carga.
   */
  public sugerenciaActual: string = '';

  /**
   * Intervalo usado para rotar sugerencias.
   */
  private intervalSugerencias: ReturnType<typeof setInterval> | null = null;

  /**
   * Lista de mensajes mostrados durante el análisis.
   */
  public sugerencias: string[] = [
    "Analizando patrones de lenguaje y estructura...",
    "Extrayendo metadatos y firmas ocultas...",
    "Consultando la red neuronal de detección...",
    "Evaluando anomalías en el contenido...",
    "Comparando contra millones de muestras..."
  ];

  /**
   * Retorna las extensiones permitidas según el tipo de herramienta.
   */
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

  /**
   * Inicia el estado de carga y rota sugerencias automáticamente.
   */
  iniciarCarga() {
    this.cargando = true;
    let i = 0;
    this.sugerenciaActual = this.sugerencias[0];

    this.intervalSugerencias = setInterval(() => {
      i = (i + 1) % this.sugerencias.length;
      this.sugerenciaActual = this.sugerencias[i];
    }, 2500);
  }

  /**
   * Detiene el estado de carga.
   */
  detenerCarga() {
    this.cargando = false;

    if (this.intervalSugerencias) {
      clearInterval(this.intervalSugerencias);
    }
  }

  /**
   * Tipo de herramienta actual.
   */
  @Input() tipoHerramienta: string = 'texto';

  /**
   * Pestaña activa en la interfaz.
   */
  activeTab: 'text' | 'file' = 'text';

  /**
   * Instancia de la gráfica Chart.js.
   */
  chartMedidor: any;

  /**
   * Mapa de modelos IA según categoría.
   */
  private mapaModelos: Record<string, ModeloIA[]> = {
    texto: [
      { nombre: 'Grok', icono: 'fa-bolt', color: '#f59e0b' },
      { nombre: 'Gemini', icono: 'fa-google', color: '#3b82f6' },
      { nombre: 'Mistral', icono: 'fa-wind', color: '#a78bfa' },
      { nombre: 'Winston', icono: 'fa-shield-halved', color: '#10b981' },
    ],
    imagen: [
      { nombre: 'Sightengine', icono: 'fa-eye', color: '#f59e0b' },
      { nombre: 'Gemini', icono: 'fa-google', color: '#3b82f6' },
      { nombre: 'Hive Moderation', icono: 'fa-shield-halved', color: '#10b981' },
      { nombre: 'Grok', icono: 'fa-bolt', color: '#a78bfa' },
    ],
    video: [
      { nombre: 'TwelveLabs', icono: 'fa-film', color: '#f59e0b' },
      { nombre: 'Hive Moderation', icono: 'fa-shield-halved', color: '#10b981' },
      { nombre: 'Gemini', icono: 'fa-google', color: '#3b82f6' },
    ],
    audio: [
      { nombre: 'ACRCloud', icono: 'fa-music', color: '#f59e0b' },
      { nombre: 'Gemini', icono: 'fa-google', color: '#3b82f6' },
    ],
    musica: [
      { nombre: 'ACRCloud', icono: 'fa-music', color: '#f59e0b' },
      { nombre: 'Gemini', icono: 'fa-google', color: '#3b82f6' },
    ],
  };

  /**
   * Modelos mostrados actualmente en pantalla.
   */
  modelosActuales: ModeloIA[] = [];

  /**
   * Inicializa modelos y suscripciones.
   */
  ngOnInit(): void {
    this.actualizarModelos();

    this.archivoService.archivoSeleccionado$.subscribe((archivoSeleccionado: ArchivoModel) => {

      const categoriaDetectada = this.determinarCategoriaPorArchivo(
        archivoSeleccionado.rutaAlmacenamiento
      );

      this.tipoHerramienta = categoriaDetectada;
      this.nombre = archivoSeleccionado.nombre ?? '';

      this.archivo = null;
      this.archivoHistorico = null;
      this.url = '';
      this.textoPlano = '';

      const esUrl = /^https?:\/\//i.test(
        archivoSeleccionado.rutaAlmacenamiento ?? ''
      );

      if (esUrl) {
        this.activeTab = 'text';
        this.url = archivoSeleccionado.rutaAlmacenamiento;
      } else {
        this.activeTab = 'file';
        this.archivoHistorico = archivoSeleccionado;
      }

      this.resultadoService
        .getMostrarResultadosPorId(archivoSeleccionado.id)
        .subscribe({
          next: (resultados: ResultadoIAModel[]) => {
            this.actualizarValoresModelos(resultados);
          },
          error: (err) => {
            console.log( err.error || "ta mal" )
          }
        });

      this.resultadoService
        .getMostrarAnalisisPorId(archivoSeleccionado.id)
        .subscribe({
          next: (analisisResp: AnalisisModel[]) => {

            if (analisisResp && analisisResp.length > 0) {
              const analisis = analisisResp[0];

              this.actualizarPorcentaje(analisis.porcentajeFinal);
              this.promedioActual = analisis.porcentajeFinal;
              this.veredictoActual = analisis.veredicto;
            }
          },
          error: (err: HttpErrorResponse) => {
            this.actualizarPorcentaje(0);
            this.promedioActual = 0;
            this.veredictoActual = 'Sin datos';
          }
        });
    });
  }

  /**
   * Elimina el archivo histórico seleccionado.
   */
  quitarArchivoHistorico(event: Event) {
    event.stopPropagation();
    this.archivoHistorico = null;
  }

  /**
   * Inicializa la gráfica después del renderizado.
   */
  ngAfterViewInit(): void {
    setTimeout(() => this.inicializarGraficaVacia(), 0);
  }

  /**
   * Detecta cambios en los inputs del componente.
   */
  ngOnChanges(changes: SimpleChanges): void {
    if (changes['tipoHerramienta']) {

      this.actualizarModelos();

      const herramienta = this.tipoHerramienta?.toLowerCase();

      if (
        herramienta === 'video' ||
        herramienta === 'audio' ||
        herramienta === 'musica'
      ) {
        this.activeTab = 'file';
      }
    }
  }

  /**
   * Actualiza la lista de modelos según la categoría.
   */
  private actualizarModelos(): void {
    const clave = this.tipoHerramienta?.toLowerCase() ?? 'texto';
    this.modelosActuales =
      this.mapaModelos[clave] ?? this.mapaModelos['texto'];
  }

  /**
   * Cambia la pestaña activa.
   */
  switchTab(tab: 'text' | 'file') {
    this.activeTab = tab;
  }

  /**
   * Inicializa la gráfica vacía.
   */
  inicializarGraficaVacia() {
    const ctx = document.getElementById('iaProbabilityChart');

    if (ctx) {
      this.chartMedidor = new Chart(ctx, {
        type: 'doughnut',
        data: {
          labels: ['IA', 'Restante'],
          datasets: [{
            data: [0, 100],
            backgroundColor: ['#f59e0b', '#27272a'],
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

  /**
   * Actualiza el porcentaje mostrado en la gráfica.
   */
  actualizarPorcentaje(nuevoPorcentaje: number) {

    if (!this.chartMedidor) {

      this.inicializarGraficaVacia();

      setTimeout(() => {
        if (this.chartMedidor) {
          this.chartMedidor.data.datasets[0].data = [
            nuevoPorcentaje,
            100 - nuevoPorcentaje
          ];

          this.chartMedidor.update();
        }
      }, 50);

      return;
    }

    this.chartMedidor.data.datasets[0].data = [
      nuevoPorcentaje,
      100 - nuevoPorcentaje
    ];

    this.chartMedidor.update();
  }

  /**
   * Reinicia completamente la gráfica.
   */
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

  /**
   * Envía un archivo local al backend.
   */
  subirArchivoLocal() {

    if (!this.archivo) {
      alert('Advertencia: Selecciona un archivo primero');
      return;
    }

    this.iniciarCarga();

    this.archivoService
      .postAnalizarArchivo(this.nombre, this.archivo)
      .subscribe({
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

            alert(
              `Error: ${err.error || 'Error al analizar el archivo.'}`
            );
          }, 1500);
        },
      });
  }

  /**
   * Envía una URL al backend para análisis.
   */
  subirArchivoUrl() {

    if (!this.url) {
      alert('Advertencia: Ingresa una URL primero');
      return;
    }

    this.iniciarCarga();

    this.archivoService
      .postAnalizarUrl(this.nombre, this.url)
      .subscribe({
        next: (resp: any) => {

          setTimeout(() => {

            this.detenerCarga();

            this.archivoService.analisisCompletado$.next();

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

            alert(
              `Error: ${err.error || 'Error al analizar url.'}`
            );
          }, 1500);
        },
      });
  }

  /**
   * Captura el archivo seleccionado.
   */
  onFileChange(event: any) {
    this.archivo = event.target.files[0];
  }

  /**
   * Elimina el archivo local seleccionado.
   */
  quitarArchivo(event: Event) {
    event.stopPropagation();
    this.archivo = null;
  }

  /**
   * Copia la URL actual al portapapeles.
   */
  async copiarUrl(): Promise<void> {

    if (!this.url) return;

    try {

      await navigator.clipboard.writeText(this.url);

      this.toastr.success(
        'URL copiada al portapapeles.',
        'Copiado'
      );

    } catch {

      this.toastr.warning(
        'No se pudo copiar al portapapeles.',
        'Error'
      );
    }
  }

  /**
   * Ejecuta el análisis dependiendo del modo activo.
   */
  ejecutarAnalisis() {

    if (this.activeTab === 'file') {

      this.subirArchivoLocal();

    } else if (
      this.activeTab === 'text' &&
      this.tipoHerramienta !== 'texto'
    ) {

      this.subirArchivoUrl();

    } else {

      this.subirTextoPlano();
    }
  }

  /**
   * Envía texto plano al backend.
   */
  subirTextoPlano() {

    if (!this.textoPlano || this.textoPlano.trim().length === 0) {
      alert('Advertencia: Pega un texto primero');
      return;
    }

    this.iniciarCarga();

    this.archivoService
      .postAnalizarTexto(this.nombre, this.textoPlano)
      .subscribe({
        next: (resp: any) => {

          setTimeout(() => {

            this.detenerCarga();

            this.archivoService.analisisCompletado$.next();

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

            alert(
              `Error: ${err.error || 'Error al analizar el texto.'}`
            );
          }, 1500);
        },
      });
  }

  /**
   * Actualiza los valores de los modelos usando datos del backend.
   */
  actualizarValoresModelos(datosBackend: any) {

    let datosNormalizados: {
      nombre: string,
      valor: number
    }[] = [];

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

    const todosLosModelosBase =
      Object.values(this.mapaModelos).flat();

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

  /**
   * Determina la categoría según la extensión del archivo.
   */
  determinarCategoriaPorArchivo(ruta: string): string {

    if (!ruta) return 'texto';

    const extension =
      ruta.split('.').pop()?.toLowerCase() || '';

    if (['txt', 'pdf', 'docx', 'doc'].includes(extension)) {
      return 'texto';
    } else if (
      ['jpg', 'jpeg', 'png', 'gif', 'webp']
        .includes(extension)
    ) {
      return 'imagen';
    } else if (
      ['mp4', 'avi', 'mov', 'mkv']
        .includes(extension)
    ) {
      return 'video';
    } else if (
      ['mp3', 'wav', 'ogg']
        .includes(extension)
    ) {
      return 'audio';
    }

    return 'texto';
  }
}
