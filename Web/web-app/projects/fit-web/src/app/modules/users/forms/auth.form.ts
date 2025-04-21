import {FormControl, FormGroup, Validators} from '@angular/forms';
import {passwordValidator} from '../validators/password.validator';


export interface LoginForm {
  login: FormControl<string>;
  rawPassword: FormControl<string>;
}

export const authForm: FormGroup<LoginForm> = new FormGroup<LoginForm>({
  login: new FormControl('', {
    nonNullable: true,
    validators: [
      Validators.required,
      Validators.pattern(/^[a-zA-Z0-9]+$/)
    ]
  }),
  rawPassword: new FormControl('', {
    nonNullable: true, validators: [
      passwordValidator(),
      Validators.required
    ]
  })
})
