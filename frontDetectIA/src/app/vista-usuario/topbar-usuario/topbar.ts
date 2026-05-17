import { Component, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'app-topbar',
  standalone: true,
  imports: [],
  templateUrl: './topbar.html',
  styleUrl: './topbar.css'
})
export class Topbar {
  @Output() menuToggle = new EventEmitter<void>();

  abrirMenu() {
    this.menuToggle.emit();
  }
}
