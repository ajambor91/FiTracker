import {Injectable} from '@angular/core';
import {ApiUsersService, DeleteUserRequest, LoginRequest, RegisterUserRequest, UpdateUserRequest} from 'api';
import {AuthService} from '../../../core/services/auth.service';
import {Router} from '@angular/router';
import {catchError, EMPTY, filter, iif, map, Observable, switchMap, take, tap} from 'rxjs';
import {User} from '../models/user.model';
import {SnackbarService} from '../../shared/services/snackbar.service';
import {FormGroup} from '@angular/forms';
import {AccountForm} from '../forms/account.form';
import {LoginForm} from '../forms/auth.form';
import {RegisterForm} from '../forms/register.form';
import {ModalService} from '../../shared/services/modal.service';
import {LoaderService} from '../../shared/services/loader.service';
import {errorUtil} from '../../../core/utils/error.util';
import {RemoveAccountForm} from '../forms/remove-account.form';


@Injectable()
export class UsersService {

  constructor(
    private apiUsersService: ApiUsersService,
    private authService: AuthService,
    private router: Router,
    private snackbarService: SnackbarService,
    private modalService: ModalService,
    private loaderService: LoaderService
  ) {
  }

  public auth(form: FormGroup<LoginForm>): void {
    if (form.invalid) {
      this.snackbarService.showError("Form has errors");
      form.markAllAsTouched();
      return;
    }
    this.loaderService.show();
    const {login, rawPassword} = form.getRawValue();
    const loginRequest: LoginRequest = {
      login, rawPassword
    }
    this.apiUsersService.loginUser(loginRequest).pipe(
      filter(nullableUser => !!nullableUser),
      tap(user => {
        const currentUser: User = {
          name: user.name,
          userId: user.userId,
          login: user.login
        }
        this.authService.loginUser(currentUser);
      }),
      catchError(err => {
        this.loaderService.hide();
        this.snackbarService.showError(errorUtil.parseError(err))
        return EMPTY;
      })
    ).subscribe(res => {
      this.loaderService.hide();
      this.router.navigate(['/dashboard'])
    });
  }

  public register(form: FormGroup<RegisterForm>): void {
    if (form.invalid) {
      this.snackbarService.showError("Form has errors");
      form.markAllAsTouched();
      return;
    }
    this.loaderService.show();
    const {name, login, email, rawPassword} = form.getRawValue();
    const registerUserRequest: RegisterUserRequest = {
      name, login, email, rawPassword
    }
    this.apiUsersService.registerUser(registerUserRequest).pipe(
      catchError(err => {
        this.loaderService.hide();
        this.snackbarService.showError(errorUtil.parseError(err))
        return EMPTY;
      })
    ).subscribe(() => {
      this.loaderService.hide();
      this.router.navigate(['/users/register/success'])
    });
  }

  public fillAccountDataForm(form: FormGroup<AccountForm>): Observable<User> {
    return this.authService.getUser().pipe(
      filter(nullableUser => !!nullableUser),
      take(1),
      map(user => user.userId),
      switchMap((id: number) => this.apiUsersService.getUser(id)),
      tap(userFromApi => {
        form.patchValue({
          name: userFromApi.name,
          email: userFromApi.email,
        })
      }),
      map(result => ({
        userId: result.userId,
        name: result.name,
        login: result.login
      }))
    )
  }

  public submitUpdateAccount(form: FormGroup<AccountForm>): void {
    if (form.invalid) {
      this.snackbarService.showError("Form has errors");
      form.markAllAsTouched();
      return;
    }
    this.loaderService.show();
    this.authService.getUser().pipe(
      filter(nullableUser => !!nullableUser),
      take(1),
      switchMap((user: User) => {
        const {userId, login} = user;
        const {name, email, rawPassword} = form.getRawValue();
        const payload: UpdateUserRequest = {
          name, login, email, rawPassword, userId
        }
        return this.apiUsersService.updateUser(payload);
      }),
      catchError(err => {
        this.loaderService.hide();
        this.snackbarService.showError(errorUtil.parseError(err))
        return EMPTY;
      })
    ).subscribe(() => {
      this.loaderService.hide();
      this.snackbarService.showSuccess("Account was successfully updated!")
    })
  }

  public deleteAccount(form: FormGroup<RemoveAccountForm>): void {
    if (form.invalid) {
      this.snackbarService.showError("Form has errors");
      form.markAllAsTouched();
      return;
    }
    const removeAccount$: Observable<void> = this.authService.getUser().pipe(
      filter(nullableUser => !!nullableUser),
      take(1),
      switchMap(user => {
        const {login, rawPassword} = form.getRawValue();
        const removeUserRequest: DeleteUserRequest = {
          login, rawPassword
        }
        return this.apiUsersService.deleteUser(removeUserRequest)
      }),
      tap(() => {
        this.snackbarService.showSuccess("Account was successfully deleted");
        this.authService.logoutUser();
        this.loaderService.hide();
      }),
      catchError(err => {
        this.loaderService.hide();
        this.snackbarService.showError(errorUtil.parseError(err))
        return EMPTY;
      })
    )

    this.modalService.openDialog("Are you sure?").pipe(
      tap(() => this.loaderService.show()),
      switchMap((isConfirmed: boolean) => iif(() => isConfirmed, removeAccount$, EMPTY))
    ).subscribe(() => this.router.navigate(["/"]));
  }


}
