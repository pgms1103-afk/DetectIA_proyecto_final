// src/app/app.ts
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet], // Import RouterOutlet
  template: '<router-outlet></router-outlet>', // Just the outlet
})
export class AppComponent {
  title = 'your-project-name';
}
