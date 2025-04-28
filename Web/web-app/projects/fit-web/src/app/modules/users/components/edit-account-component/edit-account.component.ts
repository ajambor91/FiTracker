import {Component, OnInit} from '@angular/core';
import {AbstractControl, FormGroup} from '@angular/forms';
import {UsersService} from '../../services/users.service';
import {SnackbarService} from '../../../shared/services/snackbar.service';
import {accountForm, AccountForm} from '../../forms/account.form';
import {Observable} from 'rxjs';
import {User} from '../../models/user.model';

@Component({
  selector: 'app-edit-account',
  standalone: false,
  templateUrl: './edit-account.component.html',
  styleUrl: './edit-account.component.scss'
})
export class EditAccountComponent implements OnInit {
  public userData$!: Observable<User>;
  private readonly _accountForm: FormGroup<AccountForm> = accountForm;

  constructor(private usersService: UsersService, private snackbar: SnackbarService) {
  }

  public get isNameInvalid(): boolean {
    const nameControl: AbstractControl = this._accountForm.get('name') as AbstractControl;
    return nameControl.invalid && nameControl.touched;
  }

  public get isEmailInvalid(): boolean {
    const emailControl: AbstractControl = this._accountForm.get('email') as AbstractControl;
    return emailControl.invalid && emailControl.touched;
  }

  public get isPasswordInvalid(): boolean {
    const passwordControl: AbstractControl = this._accountForm.get('rawPassword') as AbstractControl;
    return passwordControl.invalid && passwordControl.touched;
  }

  public get accountForm(): FormGroup<AccountForm> {
    return this._accountForm;
  }

  public ngOnInit(): void {
    this.userData$ = this.usersService.fillAccountDataForm(this._accountForm);
  }

  public submitForm(): void {
    this.usersService.submitUpdateAccount(this.accountForm)
  }
}
