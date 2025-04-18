import {Injectable} from '@angular/core';
import {
  AddCategoryResponse,
  AddExpenseResponse,
  CategoriesApiService,
  ExpensesApiService,
  GetCategoriesResponse,
  SummarySum,
  SummarySums,
  TopExpenses,
  TotalSummariesByCategory,
  TotalSummariesByDate
} from "api"
import {forkJoin, map, Observable, tap} from 'rxjs';
import {InitialExpenses} from '../models/initial-expenses.model';
import {AddExpenseCategoryForm} from '../forms/add-expense-category.form';
import {FormGroup} from '@angular/forms';
import {AddExpenseForm, AddExpenseMultiCategoriesForm} from '../forms/add-expense.form';
import {Currency} from '../types/currency.type';
import {Dateutil} from '../../shared/utils/date.util';
import {TransformedSum} from '../models/transformed-sum.model';
import {ZoneService} from './zone.service';
import {Store} from '@ngrx/store';
import {ZonesState} from '../store/zone.reducer';
import {refreshZone} from '../store/zone.actions';

@Injectable()
export class ExpensesService {
  constructor(
    private expensesApiService: ExpensesApiService,
    private categoriesApiService: CategoriesApiService,
    private store: Store<ZonesState>) {
  }

  public getInitialData(zoneId: string, date?: Date): Observable<InitialExpenses> {
    let from: string | undefined = undefined;
    let end: string | undefined = undefined;
    if (date) {
      from = Dateutil.getStart(date);
      end = Dateutil.getEnd(date);
    }
    return forkJoin([this.getTopExpenses(zoneId, from, end), this.getSummaryByCategory(zoneId, 'month', from, end), this.getSummaryByDate(zoneId, 'day', from, end), this.getSummarySum(zoneId, 'month', from, end)]).pipe(map(([expenses, byCategory, byDate, sum]) => ({
      byCategory: byCategory.summaries,
      byDate: byDate.summaries,
      expenses: expenses.expenses,
      sum: sum.sum.reduce((acc: TransformedSum, curr: SummarySum) => {
        if (!acc.overallSum || !acc.sum) {
          acc = {
            sum: [{
              categoryName: curr.categoryName,
              categoryValue: curr.categoryValue
            }],
            overallSum: curr.overallSum
          }
        } else {
          acc.sum.push({
            categoryValue: curr.categoryValue,
            categoryName: curr.categoryName
          })
        }
        return acc;
      }, {} as TransformedSum)
    })))
  }

  public getTopExpenses(zoneId: string, from?: string, end?: string, currency: Currency = "PLN", userId?: number): Observable<TopExpenses> {
    return this.expensesApiService.getTopExpenses(zoneId, from, end, currency, userId);
  }

  public getSummarySum(zoneId: string, periodType: string = 'month', from?: string, end?: string, currency: Currency = "PLN", userId?: number): Observable<SummarySums> {
    return this.expensesApiService.getSummarySum(zoneId, periodType, from, end, currency, userId);
  }

  public getSummaryByDate(zoneId: string, periodType: string = "day", from?: string, end?: string, currency: Currency = "PLN", userId?: number): Observable<TotalSummariesByDate> {
    return this.expensesApiService.getSummaryByDate(zoneId, periodType, from, end, currency, userId);
  }

  public getSummaryByCategory(zoneId: string, periodType: string = "day", from?: string, end?: string, currency: Currency = "PLN", userId?: number): Observable<TotalSummariesByCategory> {
    return this.expensesApiService.getSummaryByCategory(zoneId, periodType, from, end, currency, userId);
  }

  public addExpenseCategory(zoneId: string, form: FormGroup<AddExpenseCategoryForm>): Observable<AddCategoryResponse> {
    const {name, description} = form.getRawValue();
    return this.categoriesApiService.addCategory({
      name, zoneId,
      description: description ?? undefined
    }, zoneId);
  }

  public addExpense(zoneId: string, categoryId: number, form: FormGroup<AddExpenseForm>): Observable<AddExpenseResponse> {
    const {name, value} = form.getRawValue();
    return this.expensesApiService.addExpense({
      name, zoneId,
      currency: 'PLN',
      value: value,
      categoriesIds: [categoryId]
    }, zoneId);
  }

  public addExpenseMultiCategories(zoneId: string, form: FormGroup<AddExpenseMultiCategoriesForm>): Observable<AddExpenseResponse> {
    const {name, value, categoriesIds} = form.getRawValue();
    return this.expensesApiService.addExpense({
      name, zoneId,
      currency: 'PLN',
      value: value,
      categoriesIds: categoriesIds.map(id => +id)
    }, zoneId).pipe(
      tap(zone => this.store.dispatch(refreshZone(zoneId)))
    );
  }

  public getCategories(zoneId: string): Observable<GetCategoriesResponse> {
    return this.categoriesApiService.getCategories(zoneId);
  }
}
