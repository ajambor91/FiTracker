import {Injectable} from '@angular/core';
import {ApiCoreService} from 'api';


@Injectable()
export class CoreService {
  constructor(private apiService: ApiCoreService) {


  }

  public getCsrfToken(): void {
    this.apiService.getCsrfToken().subscribe();
  }
}
