import {Component} from '@angular/core';
import {FormGroup} from '@angular/forms';
import {UsersService} from '../../services/users.service';
import {registerForm, RegisterForm} from '../../forms/register.form';
import {RegisterUserRequest} from 'api';

@Component({
  selector: 'app-register',
  standalone: false,
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss'
})
export class RegisterComponent {
  private readonly _registerForm: FormGroup<RegisterForm> = registerForm;

  constructor(private usersService: UsersService) {
  }

  public get registerForm(): FormGroup<RegisterForm> {
    return this._registerForm;
  }

  public submitForm(): void {
    this.usersService.register(this.registerForm.getRawValue() as RegisterUserRequest)
  }
}
