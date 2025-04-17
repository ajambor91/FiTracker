export interface TopExpense {
  expenseName: string;
  expenseValue: number;
  categoryName: string;
  date: string;
}

export interface TopExpenses {
  expenses: TopExpense[]
}
