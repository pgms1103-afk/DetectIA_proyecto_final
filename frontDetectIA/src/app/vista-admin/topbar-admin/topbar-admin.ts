import { Component, Output, EventEmitter } from '@angular/core';
import { CommonModule} from '@angular/common';

@Component({
  selector: 'app-topbar-admin',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './topbar-admin.html',
  styleUrl: './topbar-admin.css',
})
export class TopbarAdmin {
  @Output() menuToggle = new EventEmitter<void>();

  showProfileMenu: boolean = false;

  user = {
    name: 'Jose Manuel',
    email: 'jose.manuel@elbosque.edu.co',
    time: '14h 20m',
    files: 28,
  };

  abrirMenu() {
    this.menuToggle.emit();
  }

  toggleProfileMenu() {
    this.showProfileMenu = !this.showProfileMenu;
  }
}
