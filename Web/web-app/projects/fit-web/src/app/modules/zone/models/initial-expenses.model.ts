import {TopExpense, TotalSummaryByCategory, TotalSummaryByDate} from "api";
import {TransformedSum} from './transformed-sum.model';

export interface InitialExpenses {
  expenses: TopExpense[];
  byCategory: TotalSummaryByCategory[];
  byDate: TotalSummaryByDate[];
  sum: TransformedSum;
}
