import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {ReactiveFormsModule} from '@angular/forms';
import {MatFormField, MatInput, MatLabel} from '@angular/material/input';
import {MatButton} from '@angular/material/button';
import {provideAnimationsAsync} from '@angular/platform-browser/animations/async';
import {FormContainerComponent} from './components/form-container/form-container.component';
import {HeaderComponent} from './components/header/header.component';
import {FontAwesomeModule} from '@fortawesome/angular-fontawesome';
import {MonthPickerComponent} from './components/month-picker/month-picker.component';
import {MatFormFieldModule} from '@angular/material/form-field';
import {ConfirmComponent} from './components/dialogs/confirm/confirm.component';
import {LoaderComponent} from './components/loader/loader.component';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {MatTab, MatTabGroup, MatTabLink, MatTabNav, MatTabNavPanel} from '@angular/material/tabs';
import {MatSelect} from '@angular/material/select';
import {MatOption} from '@angular/material/core';
import {MatList, MatListItem} from '@angular/material/list';
import {MatDialogModule} from '@angular/material/dialog';


@NgModule({
  declarations: [
    FormContainerComponent,
    HeaderComponent,
    MonthPickerComponent,
    ConfirmComponent,
    LoaderComponent
  ],
  imports: [
    MatProgressSpinnerModule,
    CommonModule,
    ReactiveFormsModule,
    FontAwesomeModule,
    MatFormField,
    MatInput,
    MatLabel,
    MatButton,
    MatFormFieldModule,
    MatTabGroup,
    MatTab, MatTabNavPanel,
    MatTabNav,
    MatSelect,
    MatOption,
    MatTabLink,
    MatList,
    MatListItem,
    MatDialogModule,


  ],
  exports: [
    MatProgressSpinnerModule,
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
    MatFormFieldModule,
    MatTabGroup,
    MatTab, MatTabNavPanel,
    MatTabNav,
    MatSelect,
    MatOption,
    MatTabLink,
    MatList,
    MatListItem,
    MatDialogModule,
  ],
  providers: [
    provideAnimationsAsync(),

  ]
})
export class SharedModule {
}
