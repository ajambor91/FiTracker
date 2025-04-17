import {Component} from '@angular/core';
import {authForm, LoginForm} from '../../forms/auth.form';
import {FormGroup} from '@angular/forms';
import {UsersService} from '../../services/users.service';
import {LoginRequest} from 'api';

@Component({
  selector: 'app-auth',
  standalone: false,
  templateUrl: './auth.component.html',
  styleUrl: './auth.component.scss'
})
export class AuthComponent {
  private readonly _authForm: FormGroup<LoginForm> = authForm;

  constructor(private usersService: UsersService) {
  }

  public get authForm(): FormGroup<LoginForm> {
    return this._authForm;
  }

  public submitForm(): void {
    this.usersService.auth(this.authForm.getRawValue() as LoginRequest)
  }
}
