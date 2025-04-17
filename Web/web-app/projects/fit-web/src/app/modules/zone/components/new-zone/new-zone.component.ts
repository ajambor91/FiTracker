import {Component} from '@angular/core';
import {newZoneForm, NewZoneForm} from '../../forms/new-zone.form';
import {FormGroup} from '@angular/forms';
import {ZoneService} from '../../services/zone.service';
import {NewZoneRequest} from 'api';

@Component({
  selector: 'app-new-zone',
  standalone: false,
  templateUrl: './new-zone.component.html',
  styleUrl: './new-zone.component.scss'
})
export class NewZoneComponent {
  constructor(private zoneService: ZoneService) {
  }

  private _form: FormGroup<NewZoneForm> = newZoneForm;

  public get form(): FormGroup<NewZoneForm> {
    return this._form;
  }

  public submitForm(): void {
    this.zoneService.addNewZone(this._form.getRawValue() as NewZoneRequest);
  }
}
