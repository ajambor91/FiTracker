export interface TotalSummaryByDate {
  date: string;
  expensesValue: number;
}

export interface TotalSummariesByDate {
  summaries: TotalSummaryByDate[];
}
