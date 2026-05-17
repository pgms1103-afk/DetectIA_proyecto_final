import { Component, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'app-topbar-admin',
  standalone: true,
  imports: [],
  templateUrl: './topbar-admin.html',
  styleUrl: './topbar-admin.css',
})
export class TopbarAdmin {
  @Output() menuToggle = new EventEmitter<void>();

  notificarToggle() {
    this.menuToggle.emit();
  }
}
