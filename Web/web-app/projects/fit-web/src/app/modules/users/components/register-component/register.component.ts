import {Component} from '@angular/core';
import {AbstractControl, FormGroup} from '@angular/forms';
import {UsersService} from '../../services/users.service';
import {registerForm, RegisterForm} from '../../forms/register.form';
import {RegisterUserRequest} from 'api';
import {SnackbarService} from '../../../shared/services/snackbar.service';

@Component({
  selector: 'app-register',
  standalone: false,
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss'
})
export class RegisterComponent {
  private readonly _registerForm: FormGroup<RegisterForm> = registerForm;

  constructor(private usersService: UsersService, private snackbar: SnackbarService) {
  }

  public get registerForm(): FormGroup<RegisterForm> {
    return this._registerForm;
  }

  public get isLoginInvalid(): boolean {
    const loginControl: AbstractControl = this.registerForm.get('login') as AbstractControl;
    return loginControl.invalid && loginControl.touched;
  }

  public get isPasswordControl(): boolean {
    const passwordControl: AbstractControl = this.registerForm.get('rawPassword') as AbstractControl;
    const repeatPasswordControl: AbstractControl = this.registerForm.get('repeatPassword') as AbstractControl;
    return (passwordControl.touched && passwordControl.invalid) || (repeatPasswordControl.invalid && repeatPasswordControl.touched);
  }

  public get isNameInvalid(): boolean {
    const nameControl: AbstractControl = this.registerForm.get('name') as AbstractControl;
    return nameControl.invalid && nameControl.touched;
  }

  public get isEmailInvalid(): boolean {
    const emailControl: AbstractControl = this.registerForm.get('email') as AbstractControl;
    return emailControl.invalid && emailControl.touched;
  }

  public submitForm(): void {
    if (this.registerForm.valid) {
      this.usersService.register(this.registerForm.getRawValue() as RegisterUserRequest)
    } else {
      this.registerForm.markAllAsTouched()
      this.snackbar.showError("Form has errors")
    }

  }
}
