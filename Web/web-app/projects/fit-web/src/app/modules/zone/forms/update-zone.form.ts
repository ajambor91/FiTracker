import {FormArray, FormControl, FormGroup, Validators} from '@angular/forms';
import {findUserForm, FindUserForm} from './find-user.form';


export interface UpdateZoneForm {
  zoneData: FormGroup<ZoneDataForm>;
  findUser: FormGroup<FindUserForm>;
  foundUsers: FormArray<FormGroup<FoundUserForm>>;
  addedMembers: FormArray<FormGroup<FoundUserForm>>;
}

export interface FoundUserForm {
  id: FormControl<number | null>;
  name: FormControl<string | null>;
  email: FormControl<string | null>;
}

export interface ZoneDataForm {
  zoneName: FormControl<string>;
  zoneDescription: FormControl<string | null>;
}

export const updateZoneForm: FormGroup<UpdateZoneForm> = new FormGroup<UpdateZoneForm>({
  zoneData: new FormGroup({
    zoneName: new FormControl<string>('', {
      nonNullable: true, validators: [
        Validators.required,
        Validators.pattern(/^[\w\d ]+$/)

      ]
    }),
    zoneDescription: new FormControl<string | null>('', {
      nonNullable: false,
      validators: [
        Validators.pattern(/^[\w\d ]+$/)
      ]
    })
  }),
  findUser: findUserForm,
  foundUsers: new FormArray<FormGroup<{
    id: FormControl<number | null>;
    name: FormControl<string | null>;
    email: FormControl<string | null>;
  }>>([]),
  addedMembers: new FormArray<FormGroup<{
    id: FormControl<number | null>;
    name: FormControl<string | null>;
    email: FormControl<string | null>;
  }>>([])
})
