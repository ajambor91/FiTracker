import {Component, OnInit} from '@angular/core';
import {AbstractControl, FormGroup} from '@angular/forms';
import {UsersService} from '../../services/users.service';
import {filter, Observable} from 'rxjs';
import {User} from '../../models/user.model';
import {RemoveAccountForm, removeAccountForm} from '../../forms/remove-account.form';
import {AuthService} from '../../../../core/services/auth.service';

@Component({
  selector: 'app-remove-account',
  standalone: false,
  templateUrl: './remove-account.component.html',
  styleUrl: './remove-account.component.scss'
})
export class RemoveAccountComponent implements OnInit {
  public userData$!: Observable<User>;
  private readonly _removeAccountForm: FormGroup<RemoveAccountForm> = removeAccountForm;

  constructor(
    private usersService: UsersService,
    private authService: AuthService,
  ) {
  }

  public get isLoginInvalid(): boolean {
    const loginControl: AbstractControl = this._removeAccountForm.get('login') as AbstractControl;
    return loginControl.invalid && loginControl.touched;
  }

  public get isPasswordInvalid(): boolean {
    const passwordControl: AbstractControl = this._removeAccountForm.get('rawPassword') as AbstractControl;
    return passwordControl.invalid && passwordControl.touched;
  }

  public get removeAccountForm(): FormGroup<RemoveAccountForm> {
    return this._removeAccountForm;
  }

  public ngOnInit(): void {
    this.userData$ = this.authService.getUser().pipe(filter(nullableUser => !!nullableUser));
  }

  public submitForm(): void {

    this.usersService.deleteAccount(this._removeAccountForm);

  }
}
