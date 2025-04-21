import {FormControl, FormGroup, Validators} from '@angular/forms';


export interface NewZoneForm {
  zoneName: FormControl<string>;
  zoneDescription: FormControl<string | null>;
}

export const newZoneForm: FormGroup<NewZoneForm> = new FormGroup<NewZoneForm>({
  zoneName: new FormControl('', {
    nonNullable: true,
    validators: [
      Validators.required,
      Validators.pattern(/^[\w\d ]+$/)

    ]
  }),
  zoneDescription: new FormControl('', {
    nonNullable: false, validators: [
      Validators.pattern(/^[\w\d ]+$/)
    ]
  })
})
