import {Component} from '@angular/core';
import {authForm, LoginForm} from '../../forms/auth.form';
import {AbstractControl, FormGroup} from '@angular/forms';
import {UsersService} from '../../services/users.service';
import {LoginRequest} from 'api';
import {SnackbarService} from '../../../shared/services/snackbar.service';

@Component({
  selector: 'app-auth',
  standalone: false,
  templateUrl: './auth.component.html',
  styleUrl: './auth.component.scss'
})
export class AuthComponent {
  private readonly _authForm: FormGroup<LoginForm> = authForm;

  constructor(private usersService: UsersService, private snackbar: SnackbarService) {
  }

  public get isLoginInvalid(): boolean {
    const loginControl: AbstractControl = this._authForm.get('login') as AbstractControl;
    return loginControl.invalid && loginControl.touched;
  }

  public get isPasswordInvalid(): boolean {
    const passwordControl: AbstractControl = this._authForm.get('rawPassword') as AbstractControl;
    return passwordControl.invalid && passwordControl.touched;
  }

  public get authForm(): FormGroup<LoginForm> {
    return this._authForm;
  }

  public submitForm(): void {
    if (this.authForm.valid) {
      this.usersService.auth(this.authForm.getRawValue() as LoginRequest)
    } else {
      this.authForm.markAllAsTouched()
      this.snackbar.showError("Form has errors");
    }

  }
}
