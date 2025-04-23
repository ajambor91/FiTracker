import {inject, NgModule, provideAppInitializer} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';

import {AppComponent} from './app.component';
import {CoreService} from './core/services/core.service';
import {provideHttpClient, withFetch, withInterceptors} from '@angular/common/http';
import {API_BASE_URL, ApiCoreService} from 'api';
import {csrfInterceptor} from './core/interceptors/csrf.interceptor';
import {coreReducer} from './core/store/core.reducer';

import {EffectsModule} from '@ngrx/effects';
import {StoreModule} from '@ngrx/store';

import {MAT_FORM_FIELD_DEFAULT_OPTIONS} from '@angular/material/form-field';
import {MainModule} from './modules/main/main.module';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {jwtResponseInterceptor} from './core/interceptors/jwt-response.interceptor';
import {AuthService} from './core/services/auth.service';
import {jwtRequestInterceptor} from './core/interceptors/jwt-request.interceptor';
import {FontAwesomeModule} from '@fortawesome/angular-fontawesome';
import {SnackbarService} from './modules/shared/services/snackbar.service';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {environment} from '../environments/environment';

@NgModule({
  declarations: [
    AppComponent,
  ],
  imports: [
    MainModule,
    BrowserModule,
    EffectsModule.forRoot([]),
    StoreModule.forRoot({core: coreReducer}),
    NgbModule,
    BrowserAnimationsModule,
    FontAwesomeModule,


  ],
  providers: [
    {provide: MAT_FORM_FIELD_DEFAULT_OPTIONS, useValue: {floatLabel: 'always'}},

    {
      provide: API_BASE_URL,
      useValue: environment.apiUrl
    },
    AuthService,
    ApiCoreService,
    CoreService,
    SnackbarService,
    provideHttpClient
    (withFetch(),
      withInterceptors([
        csrfInterceptor,
        jwtResponseInterceptor,
        jwtRequestInterceptor
      ])),
    provideAppInitializer(() => {
      const store: CoreService = inject(CoreService);
      store.getCsrfToken();
    })

  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
