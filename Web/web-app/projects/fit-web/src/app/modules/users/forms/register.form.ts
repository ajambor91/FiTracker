import {FormControl, FormGroup} from '@angular/forms';


export interface RegisterForm {
  name: FormControl<string>;
  login: FormControl<string>;
  rawPassword: FormControl<string>;
}

export const registerForm: FormGroup<RegisterForm> = new FormGroup<RegisterForm>({
  name: new FormControl('', {nonNullable: true}),
  login: new FormControl('', {nonNullable: true}),
  rawPassword: new FormControl('', {nonNullable: true})
})
