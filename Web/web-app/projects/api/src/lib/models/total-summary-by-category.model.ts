export interface TotalSummaryByCategory {
  categoryName: string;
  expensesValue: number;
}

export interface TotalSummariesByCategory {
  summaries: TotalSummaryByCategory[];
}
