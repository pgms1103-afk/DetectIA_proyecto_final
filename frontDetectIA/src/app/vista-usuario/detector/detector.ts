import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-detector',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './detector.html',
  styleUrl: './detector.css',
})
export class Detector {
  activeTab: 'text' | 'file' = 'text';
  switchTab(tab: 'text' | 'file') { this.activeTab = tab; }
}
