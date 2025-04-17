import {Inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {RegisterUserRequest} from '../models/register-user-request.model';
import {RegisterSuccess} from '../models/register-success.model';
import {LoginRequest} from '../models/login-request.model';
import {LoginResponseSuccess} from '../models/login-response-success.model';
import {BaseService} from './base.service';
import {API_BASE_URL} from '../core/tokens';
import {FindUserResponse} from '../models/find-user-response.model';
import {ZoneMember} from '../models/zone-member.model';

@Injectable()
export class ApiUsersService extends BaseService {
  protected readonly PATH = '/main/users/';

  constructor(private httpClient: HttpClient, @Inject(API_BASE_URL) private apiBaseUrl: string) {
    super(apiBaseUrl);
  }

  public registerUser(user: RegisterUserRequest): Observable<RegisterSuccess> {
    return this.httpClient.post<RegisterSuccess>(`${this.path}register`, user);
  }

  public loginUser(user: LoginRequest): Observable<LoginResponseSuccess> {
    return this.httpClient.post<LoginResponseSuccess>(`${this.path}login`, user);
  }

  public findUserByEmail(email: string): Observable<FindUserResponse> {
    return this.httpClient.get<FindUserResponse>(`${this.path}user/find?email=${email}`);
  }

  public findUserByIds(members: ZoneMember[]): Observable<FindUserResponse> {
    let path: string = '';
    members.forEach(member => {
      if (!!path) {
        path += '&'
      }
      path += `ids=${member.userId}`

    })
    return this.httpClient.get<FindUserResponse>(`${this.path}user/find/multi?${path}`);
  }
}
