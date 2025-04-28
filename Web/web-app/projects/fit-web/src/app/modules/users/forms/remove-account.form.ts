import {FormControl, FormGroup, Validators} from '@angular/forms';
import {passwordValidator} from '../validators/password.validator';


export interface RemoveAccountForm {
  login: FormControl<string>;
  rawPassword: FormControl<string>;
}


export const removeAccountForm: FormGroup<RemoveAccountForm> = new FormGroup<RemoveAccountForm>({
  login: new FormControl('', {
    nonNullable: true, validators: [
      Validators.pattern(/^[\w\d ]+$/),
      Validators.required
    ]
  }),
  rawPassword: new FormControl('', {
    nonNullable: true, validators: [
      Validators.required,
      passwordValidator()
    ]
  }),
})


