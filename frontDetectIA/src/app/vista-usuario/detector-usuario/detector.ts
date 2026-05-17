import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-detector',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './detector.html',
  styleUrl: './detector.css'
})
export class Detector {

  @Input() tipoHerramienta: string = 'texto';

  activeTab: 'text' | 'file' = 'text';

  switchTab(tab: 'text' | 'file') {
    this.activeTab = tab;
  }
}
