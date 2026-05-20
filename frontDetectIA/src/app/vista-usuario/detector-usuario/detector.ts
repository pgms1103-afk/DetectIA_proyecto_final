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
  public nombre: string = '';
  public archivo: File | null = null;
  public url: string = '';
  public promedioActual: string='';
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
      console.warn('Selecciona un archivo primero');
      return;
    }

    this.archivoService.postAnalizarArchivo(this.nombre, this.archivo).subscribe({
      next: (resp: any) => {
        console.log('Respuesta del servidor:', resp);

        // 1. Actualizar la gráfica central (el doughnut chart)
        this.actualizarPorcentaje(resp.promedio);

        // 2. Actualizar el centro del medidor en el HTML (opcional, si tienes una variable para esto)
         this.promedioActual = resp.promedio;

         this.veredictoActual = resp.veredicto;

        // 3. Actualizar la lista de modelos con sus valores individuales
        this.actualizarValoresModelos(resp.resultados);


      },
      error: (err) => {
        console.error('Error al subir el archivo:', err);
      },
    });
  }

  subirArchivoUrl() {
    if (!this.url) {
      console.warn('Ingresa una URL primero');
      return;
    }
    this.archivoService.postAnalizarUrl(this.nombre, this.url).subscribe({
      next: (resp) => {
        console.log(resp);
      },
      error: (err) => {
        console.error(err);
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
      console.warn('Para texto directo necesitas crear un método/endpoint específico.');
    }
  }

  actualizarValoresModelos(resultadosBackend: any) {
    // Recorremos los modelos que se están mostrando en pantalla
    this.modelosActuales = this.modelosActuales.map(modelo => {
      // Buscamos si el backend devolvió un valor para este modelo específico
      const valor = resultadosBackend[modelo.nombre];

      return {
        ...modelo,
        // Si hay valor lo guardamos, si no, lo dejamos en 0 o nulo
        valor: valor !== undefined ? valor : null
      };
    });
  }
}
