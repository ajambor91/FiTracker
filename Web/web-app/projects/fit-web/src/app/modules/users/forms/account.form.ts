import {FormControl, FormGroup, Validators} from '@angular/forms';
import {passwordValidator} from '../validators/password.validator';


export interface AccountForm {
  name: FormControl<string | null>;
  email: FormControl<string | null>;
  rawPassword: FormControl<string>;
}


export const accountForm: FormGroup<AccountForm> = new FormGroup<AccountForm>({
  name: new FormControl('', {
    nonNullable: false, validators: [
      Validators.pattern(/^[\w\d ]+$/),
    ]
  }),
  email: new FormControl('', {
    nonNullable: false, validators: [
      Validators.email
    ]
  }),
  rawPassword: new FormControl('', {
    nonNullable: true, validators: [
      Validators.required,
      passwordValidator()
    ]
  }),
})


