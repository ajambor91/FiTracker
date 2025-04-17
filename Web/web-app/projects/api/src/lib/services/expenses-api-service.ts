import {BaseService} from './base.service';
import {Inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {API_BASE_URL} from '../core/tokens';
import {Observable} from 'rxjs';
import {TopExpenses} from '../models/top-expenses.model';
import {TotalSummariesByCategory} from '../models/total-summary-by-category.model';
import {TotalSummariesByDate} from '../models/total-summary-by-date.model';
import {AddExpenseRequest} from '../models/add-expense-request.model';
import {AddExpenseResponse} from '../models/add-expense-response.model';
import {Currency} from '../types/currency.type';
import {SummarySums} from '../models/summary-sum.model';

@Injectable()
export class ExpensesApiService extends BaseService {
  protected readonly PATH = '/expenses/expenses/';

  constructor(private httpClient: HttpClient, @Inject(API_BASE_URL) private apiBaseUrl: string) {
    super(apiBaseUrl);
  }

  public getTopExpenses(zoneId: string, from?: string, end?: string, currency: Currency = "PLN", userId?: number): Observable<TopExpenses> {
    let param: string = `?currency=${currency}`;
    if (!!from) {
      param += `&periodStart=${from}`;
    }
    if (!!end) {
      param += `&periodEnd=${end}`;
    }
    if (!!userId) {
      param += `&userId=${userId}`;
    }
    return this.httpClient.get<TopExpenses>(`${this.path}zone/${zoneId}/summary/top${param}`);
  }

  public getSummaryByDate(zoneId: string, periodType: string = "day", from?: string, end?: string, currency: Currency = "PLN", userId?: number): Observable<TotalSummariesByDate> {
    let params: string = `?groupBy=${periodType}&currency=${currency}`;
    if (!!from) {
      params += `&periodStart=${from}`;
    }
    if (!!end) {
      params += `&periodEnd=${end}`;
    }
    if (!!userId) {
      params += `&userId=${userId}`;
    }
    return this.httpClient.get<TotalSummariesByDate>(`${this.path}zone/${zoneId}/summary${params}`);
  }

  public getSummaryByCategory(zoneId: string, periodType: string = "day", from?: string, end?: string, currency: Currency = "PLN", userId?: number): Observable<TotalSummariesByCategory> {
    let params: string = `?groupBy=category&currency=${currency}`;
    if (!!from) {
      params += `&periodStart=${from}`;
    }
    if (!!end) {
      params += `&periodEnd=${end}`;
    }
    if (!!userId) {
      params += `&userId=${userId}`;
    }
    if (!!periodType) {
      params += `&periodType=${periodType}`;
    }
    return this.httpClient.get<TotalSummariesByCategory>(`${this.path}zone/${zoneId}/summary${params}`);
  }

  public getSummarySum(zoneId: string, periodType: string = "day", from?: string, end?: string, currency: Currency = "PLN", userId?: number): Observable<SummarySums> {
    let params: string = `?groupBy=category&currency=${currency}`;
    if (!!from) {
      params += `&periodStart=${from}`;
    }
    if (!!end) {
      params += `&periodEnd=${end}`;
    }
    if (!!userId) {
      params += `&userId=${userId}`;
    }
    if (!!periodType) {
      params += `&periodType=${periodType}`;
    }
    return this.httpClient.get<SummarySums>(`${this.path}zone/${zoneId}/sum${params}`);
  }

  public addExpense(addExpense: AddExpenseRequest, zoneId: string): Observable<AddExpenseResponse> {
    return this.httpClient.post<AddExpenseResponse>(`${this.path}zone/${zoneId}/expense`, addExpense);
  }
}
