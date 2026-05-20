import { Component, inject, Input, OnInit, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ArchivoService } from '../../services/archivo.service';
import { FormsModule } from '@angular/forms';

declare var Chart: any;

@Component({
  selector: 'app-detector',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './detector.html',
  styleUrl: './detector.css',
})
export class Detector implements OnInit, AfterViewInit {
  private archivoService: ArchivoService = inject(ArchivoService);
  public nombre: string = '';
  public archivo: File | null = null;
  public url: string = '';

  @Input() tipoHerramienta: string = 'texto';

  activeTab: 'text' | 'file' = 'text';

  chartMedidor: any;

  switchTab(tab: 'text' | 'file') {
    this.activeTab = tab;
  }

  ngOnInit(): void {}

  ngAfterViewInit() {
    this.inicializarGraficaVacia();
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
      next: (resp) => {
        console.log(resp);
      },
      error: (err) => {
        console.error(err);
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
}
