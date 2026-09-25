import { ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';

import { routes } from './app.routes';
import { authInterceptor } from './core/interceptors/auth.interceptor'; // (ou o caminho onde seu authInterceptor já está)
import { apiUrlInterceptor } from './core/interceptors/api-url.interceptor'; // <-- ADICIONE ESTA LINHA

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    // Adicione o apiUrlInterceptor ANTES do authInterceptor
    provideHttpClient(withInterceptors([apiUrlInterceptor, authInterceptor])) 
  ]
};