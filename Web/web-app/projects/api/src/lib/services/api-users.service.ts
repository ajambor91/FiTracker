import {Inject, Injectable} from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs';
import {RegisterUserRequest} from '../models/register-user-request.model';
import {RegisterSuccess} from '../models/register-success.model';
import {LoginRequest} from '../models/login-request.model';
import {LoginResponseSuccess} from '../models/login-response-success.model';
import {BaseService} from './base.service';
import {API_BASE_URL} from '../core/tokens';

@Injectable()
export class ApiUsersService extends BaseService{
  protected readonly PATH = '/main/users/';
  constructor(private httpClient: HttpClient,  @Inject(API_BASE_URL) private apiBaseUrl: string ) {
    super(apiBaseUrl);
  }

  public registerUser(user: RegisterUserRequest): Observable<RegisterSuccess> {
    return this.httpClient.post<RegisterSuccess>(`${this.path}register`, user);
  }

  public loginUser(user: LoginRequest): Observable<LoginResponseSuccess> {
    return this.httpClient.post<LoginResponseSuccess>(`${this.path}login`, user);
  }
}
