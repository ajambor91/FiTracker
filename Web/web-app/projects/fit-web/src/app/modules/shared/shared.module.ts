import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {ReactiveFormsModule} from '@angular/forms';
import {MatFormField, MatInput, MatLabel} from '@angular/material/input';
import {MatButton} from '@angular/material/button';
import {provideAnimationsAsync} from '@angular/platform-browser/animations/async';
import {providePrimeNG} from 'primeng/config';
import Aura from '@primeng/themes/aura';
import {FormContainerComponent} from './components/form-container/form-container.component';
import {HeaderComponent} from './components/header/header.component';
import {FontAwesomeModule} from '@fortawesome/angular-fontawesome';
import {MonthPickerComponent} from './components/month-picker/month-picker.component';
import {SnackbarService} from './services/snackbar.service';


@NgModule({
  declarations: [
    FormContainerComponent,
    HeaderComponent,
    MonthPickerComponent
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FontAwesomeModule,
    MatFormField,
    MatInput,
    MatLabel,
    MatButton


  ],
  exports: [
    FontAwesomeModule,
    CommonModule,
    ReactiveFormsModule,
    MatFormField,
    MatInput,
    MatLabel,
    MatButton,
    FormContainerComponent,
    HeaderComponent,
    MonthPickerComponent,




  ],
  providers: [
    provideAnimationsAsync(),
    providePrimeNG({
      theme: {
        preset: Aura
      }
    }),

  ]
})
export class SharedModule {
}
