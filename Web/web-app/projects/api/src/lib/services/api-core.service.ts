import {Inject, Injectable} from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs';
import {BaseService} from './base.service';
import {API_BASE_URL} from '../core/tokens';

@Injectable()
export class ApiCoreService extends BaseService{
  protected readonly PATH = '/main/core/';
  constructor(@Inject(API_BASE_URL) private apiBaseUrl: string,
              private httpClient: HttpClient) {
    super(apiBaseUrl);
  }

  public getCsrfToken(): Observable<void> {
    return this.httpClient.get<void>(`${this.path}csrf-token`);
  }

}
