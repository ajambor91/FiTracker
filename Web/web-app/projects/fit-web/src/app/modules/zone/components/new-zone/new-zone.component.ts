import {Component} from '@angular/core';
import {newZoneForm, NewZoneForm} from '../../forms/new-zone.form';
import {AbstractControl, FormGroup} from '@angular/forms';
import {ZoneService} from '../../services/zone.service';
import {SnackbarService} from '../../../shared/services/snackbar.service';

@Component({
  selector: 'app-new-zone',
  standalone: false,
  templateUrl: './new-zone.component.html',
  styleUrl: './new-zone.component.scss'
})
export class NewZoneComponent {

  constructor(private zoneService: ZoneService, private snackbar: SnackbarService) {
  }

  private _form: FormGroup<NewZoneForm> = newZoneForm;

  public get form(): FormGroup<NewZoneForm> {
    return this._form;
  }

  public get isNameInvalid(): boolean {
    const nameControl: AbstractControl = this._form.get('zoneName') as AbstractControl;
    return nameControl.invalid && nameControl.touched;
  }

  public get isDescriptionInvalid(): boolean {
    const descriptionControl: AbstractControl = this._form.get('zoneDescription') as AbstractControl;
    return descriptionControl.invalid && descriptionControl.touched;
  }

  public submitForm(): void {
    this.zoneService.addNewZone(this._form);


  }
}
