import {FormControl, FormGroup} from '@angular/forms';


export interface LoginForm {
  login: FormControl<string>;
  rawPassword: FormControl<string>;
}

export const authForm: FormGroup<LoginForm> = new FormGroup<LoginForm>({
  login: new FormControl('', {nonNullable: true}),
  rawPassword: new FormControl('', {nonNullable: true})
})
