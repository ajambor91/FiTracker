import {FormControl, FormGroup} from '@angular/forms';


export interface NewZoneForm {
  zoneName: FormControl<string>;
  zoneDescription: FormControl<string | null>;
}

export const newZoneForm: FormGroup<NewZoneForm> = new FormGroup<NewZoneForm>({
  zoneName: new FormControl('', {nonNullable: true}),
  zoneDescription: new FormControl('', {nonNullable: false})
})
