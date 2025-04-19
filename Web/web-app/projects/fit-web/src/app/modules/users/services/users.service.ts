import {Injectable} from '@angular/core';
import {ApiUsersService, LoginRequest, RegisterUserRequest} from 'api';
import {AuthService} from '../../../core/services/auth.service';
import {Router} from '@angular/router';
import {catchError, EMPTY, tap} from 'rxjs';
import {User} from '../models/user.model';
import {SnackbarService} from '../../shared/services/snackbar.service';

@Injectable()
export class UsersService {

  constructor(
    private apiUsersService: ApiUsersService,
    private authService: AuthService,
    private router: Router,
    private snackbar: SnackbarService
  ) {
  }

  public auth(form: LoginRequest): void {
    this.apiUsersService.loginUser(form).pipe(
      tap(user => {
        const currentUser: User = {
          name: user.name
        }
        this.authService.loginUser(currentUser);
      }),
      catchError(err => {
        this.snackbar.showError("Cannot login");
        return EMPTY;
      }),
    ).subscribe(res => {
      this.router.navigate(['/dashboard'])
    });
  }

  public register(form: RegisterUserRequest): void {
    this.apiUsersService.registerUser(form).subscribe(() => {
      this.router.navigate(['/users/register/success'])
    });
  }
}
