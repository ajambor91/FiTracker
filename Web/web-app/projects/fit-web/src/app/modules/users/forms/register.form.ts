import {FormControl, FormGroup, Validators} from '@angular/forms';
import {passwordValidator} from '../validators/password.validator';


export interface RegisterForm {
  name: FormControl<string>;
  login: FormControl<string>;
  email: FormControl<string>;
  rawPassword: FormControl<string>;
  repeatPassword: FormControl<string>
}


export const registerForm: FormGroup<RegisterForm> = new FormGroup<RegisterForm>({
  name: new FormControl('', {
    nonNullable: true, validators: [
      Validators.required,
      Validators.pattern(/^[\w\d ]+$/),
      Validators.required
    ]
  }),
  login: new FormControl('', {
    nonNullable: true, validators: [
      Validators.pattern(/^[a-zA-Z0-9]+$/),
      Validators.required
    ]
  }),
  email: new FormControl('', {
    nonNullable: true, validators: [
      Validators.required,
      Validators.email
    ]
  }),
  rawPassword: new FormControl('', {
    nonNullable: true, validators: [
      Validators.required,
      passwordValidator()
    ]
  }),
  repeatPassword: new FormControl('', {
    nonNullable: true, validators: [
      Validators.required,
      passwordValidator()
    ]
  })
})
