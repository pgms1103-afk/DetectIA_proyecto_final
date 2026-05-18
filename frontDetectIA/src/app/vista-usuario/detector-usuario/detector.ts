import { Component, inject, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ArchivoService } from '../../services/archivo.service';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-detector',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './detector.html',
  styleUrl: './detector.css',
})
export class Detector implements OnInit {
  private archivoService: ArchivoService = inject(ArchivoService);
  public nombre: string = '';
  public archivo: File | null = null;
  public url: string = '';

  @Input() tipoHerramienta: string = 'texto';

  activeTab: 'text' | 'file' = 'text';

  switchTab(tab: 'text' | 'file') {
    this.activeTab = tab;
  }

  ngOnInit(): void {}

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
