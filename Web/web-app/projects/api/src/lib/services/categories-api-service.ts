import {BaseService} from './base.service';
import {API_BASE_URL} from '../core/tokens';
import {HttpClient} from '@angular/common/http';
import {Inject, Injectable} from '@angular/core';
import {AddCategoryRequest} from '../models/add-category-request.model';
import {Observable} from 'rxjs';
import {AddCategoryResponse} from '../models/add-category-response.model';
import {GetCategoriesResponse} from '../models/get-categories-response.model';
import {UpdateCategoryRequest} from '../models/update-category-request.model';
import {UpdateCategoryResponse} from '../models/update-category-response.model';

@Injectable()
export class CategoriesApiService extends BaseService {
  protected readonly PATH = '/expenses/categories/';

  constructor(private httpClient: HttpClient, @Inject(API_BASE_URL) private apiBaseUrl: string) {
    super(apiBaseUrl);
  }

  public addCategory(addCategoryRequest: AddCategoryRequest, zoneId: string): Observable<AddCategoryResponse> {
    return this.httpClient.post<AddCategoryResponse>(`${this.path}category/zone/${zoneId}`, addCategoryRequest);
  }

  public getCategories(zoneId: string): Observable<GetCategoriesResponse> {
    return this.httpClient.get<GetCategoriesResponse>(`${this.path}categories/zone/${zoneId}`);
  }

  public updateCategory(zoneId: string, categoryId: number, updateCategoryRequest: UpdateCategoryRequest): Observable<UpdateCategoryResponse> {
    return this.httpClient.patch<UpdateCategoryResponse>(`${this.path}category/${categoryId}/zone/${zoneId}`, updateCategoryRequest);
  }
}
