import { Component, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-topbar',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './topbar.html',
  styleUrl: './topbar.css'
})
export class Topbar {
  @Output() menuToggle = new EventEmitter<void>();

  showProfileMenu: boolean = false;

  user = {
    name: 'Jose Manuel',
    email: 'jose.manuel@elbosque.edu.co',
    time: '14h 20m',
    files: 28
  };

  abrirMenu() {
    this.menuToggle.emit();
  }

  toggleProfileMenu() {
    this.showProfileMenu = !this.showProfileMenu;
  }
}
