import { ApplicationConfig, provideZoneChangeDetection, importProvidersFrom} from '@angular/core';
import { provideRouter } from '@angular/router';
import { routes } from './app.routes';
import { provideAnimations} from '@angular/platform-browser/animations';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { authInterceptor } from './interceptors/auth.interceptor';
import { provideToastr } from 'ngx-toastr'; // Necesario

export const appConfig: ApplicationConfig = {
  providers: [
      provideAnimations(), // Añade esto
      provideToastr(),
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideHttpClient(withInterceptors([authInterceptor])),
    provideAnimations(),
  ],
};
