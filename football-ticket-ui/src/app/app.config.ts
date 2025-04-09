import {ApplicationConfig, importProvidersFrom, provideZoneChangeDetection} from '@angular/core';
import {provideRouter, withHashLocation} from '@angular/router';

import {routes} from './app.routes';
import {provideClientHydration, withNoHttpTransferCache} from '@angular/platform-browser';
import {
  HTTP_INTERCEPTORS,
  HttpClient,
  provideHttpClient,
  withFetch,
  withInterceptorsFromDi,
} from '@angular/common/http';
import {TranslateLoader, TranslateModule} from '@ngx-translate/core';
import {TranslateHttpLoader} from '@ngx-translate/http-loader';
import {provideAnimations} from '@angular/platform-browser/animations';
import {ModalModule} from 'ngx-bootstrap/modal';
import {provideAnimationsAsync} from '@angular/platform-browser/animations/async';
import {API_URL, CONSTANT} from './share/constant';
import {JwtInterceptor} from './share/helper/jwt-interceptor';
import {ErrorInterceptor} from './share/helper/error-interceptor';
import {provideToastr} from 'ngx-toastr';
import {provideCharts, withDefaultRegisterables} from 'ng2-charts';
import {providePrimeNG} from 'primeng/config';
import Aura from '@primeng/themes/aura';

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({eventCoalescing: true}),
    provideAnimationsAsync(),
    provideRouter(routes, withHashLocation()),
    provideClientHydration(withNoHttpTransferCache()),
    provideHttpClient(withFetch(), withInterceptorsFromDi()),
    provideAnimations(),
    importProvidersFrom(ModalModule.forRoot()),
    importProvidersFrom(TranslateModule.forRoot(
      {
        loader: {
          provide: TranslateLoader,
          useFactory: (http: HttpClient) => new TranslateHttpLoader(http, API_URL.translatePath, '.json'),
          deps: [HttpClient],
        },
        defaultLanguage: CONSTANT.defaultLocale,
      },
    )),
    {provide: HTTP_INTERCEPTORS, useClass: JwtInterceptor, multi: true},
    {provide: HTTP_INTERCEPTORS, useClass: ErrorInterceptor, multi: true},
    provideCharts(withDefaultRegisterables()),
    provideToastr(), provideCharts(withDefaultRegisterables()),
    providePrimeNG({
      theme: {
        preset: Aura,
        options: {
          prefix: 'p',
          cssLayer: false
        }
      }
    }),
  ]
};
