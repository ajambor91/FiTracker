import {Currency} from '../types/currency.type';

export interface AddExpenseResponse {
  expenseId: number;
  zoneId: string;
  currency: Currency;
  value: number;
  name: string;
  categoriesIds: number[]
}
