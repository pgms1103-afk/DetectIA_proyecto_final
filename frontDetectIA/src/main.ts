// src/main.ts
import { bootstrapApplication } from '@angular/platform-browser';
import { AppComponent } from './app/app'; // Root component
import { appConfig } from './app/app.config'; // Application config

bootstrapApplication(AppComponent, appConfig)
  .catch((err) => console.error(err));
