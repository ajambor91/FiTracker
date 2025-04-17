import {Currency} from '../types/currency.type';

export interface AddExpenseRequest {
  zoneId: string;
  currency: Currency;
  value: number;
  name: string;
  categoriesIds: number[]
}
