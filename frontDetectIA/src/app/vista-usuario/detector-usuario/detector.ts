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
import { ToastrService } from 'ngx-toastr';

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
  private toastr: ToastrService = inject(ToastrService);
  public nombre: string = '';
  public archivo: File | null = null;
  public url: string = '';
  public promedioActual: number = 0;
  public veredictoActual: string = '';


  // Getter para definir las extensiones permitidas según la herramienta actual

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
        return '*/*'; // Si no coincide nada, permite todo
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

    // 🟢 El Detector se queda escuchando si alguien hace click en el Sidebar
    this.archivoService.archivoSeleccionado$.subscribe((archivo: ArchivoModel) => {
      console.log('2. Detector: Escuché el archivo:', archivo.nombre);

      // 🟢 NUEVO: Detectar la extensión y cambiar la pestaña visual automáticamente
      const categoriaDetectada = this.determinarCategoriaPorArchivo(archivo.rutaAlmacenamiento);
      this.tipoHerramienta = categoriaDetectada;

      // 1. Petición para traer los resultados individuales de las IAs
      this.resultadoService.getMostrarResultadosPorId(archivo.id).subscribe({
        next: (resultados: ResultadoIAModel[]) => {
          console.log("3. Detector: Datos de IAs traídos con éxito", resultados);
          // ¡Magia! Le pasamos el arreglo a tu método y la lista de IAs se actualiza visualmente
          this.actualizarValoresModelos(resultados);
        },
        error: (err) => {
          this.toastr.error(err.error || "Error al traer los detalles de las IAs", 'Error');
        }
      });

      // 2. NUEVA Petición para traer el análisis general
      this.resultadoService.getMostrarAnalisisPorId(archivo.id).subscribe({
        // 🟢 Le decimos que va a recibir el arreglo completo
        next: (analisisResp: AnalisisModel[]) => {
          console.log("4. Detector: Análisis general traído con éxito", analisisResp);

          // 🟢 Validamos que el arreglo traiga al menos un elemento
          if (analisisResp && analisisResp.length > 0) {
            const analisis = analisisResp[0]; // Extraemos el objeto real
            // Actualizamos la gráfica de dona y los textos del medidor
            this.actualizarPorcentaje(analisis.porcentajeFinal);
            this.promedioActual = analisis.porcentajeFinal;
            this.veredictoActual = analisis.veredicto;
          }
        },
        error: (err: any) => {
          // Escudo protector visual
          this.actualizarPorcentaje(0);
          this.promedioActual = 0;
          this.veredictoActual = 'Sin datos';
          this.toastr.error(err.error || "Error al traer el análisis general", 'Error');
        }
      });
    });
  }

  ngAfterViewInit(): void {
    this.inicializarGraficaVacia();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['tipoHerramienta']) {
      this.actualizarModelos();
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
    if(this.chartMedidor) {
      this.chartMedidor.data.datasets[0].data = [nuevoPorcentaje, 100 - nuevoPorcentaje];
      this.chartMedidor.update();
    }
  }

  subirArchivoLocal() {
    if (!this.archivo) {
      this.toastr.warning('Selecciona un archivo primero','Advertencia');
      return;
    }

    this.archivoService.postAnalizarArchivo(this.nombre, this.archivo).subscribe({
      next: (resp: any) => {
        this.archivoService.analisisCompletado$.next(); // ← agrega esta línea
        this.toastr.warning('Respuesta del servidor:', resp);

        // 1. Actualizar la gráfica central (el doughnut chart)
        this.actualizarPorcentaje(resp.porcentajeFinal);

        // 2. Actualizar el centro del medidor en el HTML (opcional, si tienes una variable para esto)
         this.promedioActual = resp.porcentajeFinal;

         this.veredictoActual = resp.veredicto;

        // 3. Actualizar la lista de modelos con sus valores individuales
        this.actualizarValoresModelos(resp.resultados);

        this.toastr.success('Análisis completado con éxito.', 'Éxito');

      },
      error: (err) => {
         this.toastr.error(err.error || 'Error al analizar el archivo.', 'Error');
      },
    });
  }

  subirArchivoUrl() {
    if (!this.url) {
      this.toastr.warning('Ingresa una URL primero', 'Advertencia');
      return;
    }
    this.archivoService.postAnalizarUrl(this.nombre, this.url).subscribe({
      next: (resp : any) => {
        this.archivoService.analisisCompletado$.next(); // ← agrega esta línea
        console.log(resp);
        this.actualizarPorcentaje(resp.promedio);

        // 2. Actualizar el centro del medidor en el HTML (opcional, si tienes una variable para esto)
        this.promedioActual = resp.promedio;

        this.veredictoActual = resp.veredicto;

        this.toastr.success("Analisis completado con éxito.", 'Exito')
      },
      error: (err) => {
        this.toastr.error(err.error || "Error al analizar la URL.", "Error");
      },
    });
  }

  onFileChange(event: any) {
    this.archivo = event.target.files[0];
  }

  ejecutarAnalisis() {
    console.log(`Iniciando análisis. Pestaña activa: ${this.activeTab}`);

    if (this.activeTab === 'file') {
      this.subirArchivoLocal();
    } else if (this.activeTab === 'text' && this.tipoHerramienta !== 'texto') {
      this.subirArchivoUrl();
    } else {
      this.toastr.warning('Para texto directo necesitas crear un método/endpoint específico.', 'Advertencia');
    }
  }

  actualizarValoresModelos(datosBackend: any) {
    // 1. Normalizar los datos: Convertimos lo que llegue a un formato estándar { nombre, valor }
    let datosNormalizados: { nombre: string, valor: number }[] = [];

    if (Array.isArray(datosBackend)) {
      // CASO A: Viene del Historial (Es un arreglo de ResultadoIADTO)
      datosNormalizados = datosBackend.map(item => ({
        nombre: item.nombreIA,
        valor: item.porcentajeIA
      }));
    } else {
      // CASO B: Viene de un Análisis Nuevo (Es un Objeto/Map)
      datosNormalizados = Object.keys(datosBackend).map(key => ({
        nombre: key,
        valor: datosBackend[key]
      }));
    }

    // 2. Traer la base de diseño (iconos y colores) buscando en TODAS las categorías
    // Juntamos todos los modelos de texto, imagen, video, etc., en una sola lista gigante
    const todosLosModelosBase = Object.values(this.mapaModelos).flat();

    // 3. Reconstruir la lista que se pinta en el HTML
    this.modelosActuales = datosNormalizados.map(dato => {

      // Buscamos si el nombre de la IA existe en nuestra lista gigante para heredar su icono
      const modeloOriginal = todosLosModelosBase.find(
        m => m.nombre.toLowerCase() === dato.nombre.toLowerCase()
      );

      if (modeloOriginal) {
        return {
          ...modeloOriginal,
          valor: dato.valor
        };
      }

      // Escudo: Si el back manda una IA que no tienes registrada, no se rompe, le pone un icono genérico
      return {
        nombre: dato.nombre,
        icono: 'fa-microchip',
        color: '#888888',
        valor: dato.valor
      };
    });
  }
  determinarCategoriaPorArchivo(ruta: string): string {
    if (!ruta) return 'texto'; // Por defecto

    // Extraemos la extensión (ej. de "archivo.jpg" sacamos "jpg")
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

    return 'texto'; // Escudo protector por si suben algo raro
  }
}
