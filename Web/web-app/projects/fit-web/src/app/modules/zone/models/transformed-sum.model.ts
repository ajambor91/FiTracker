export interface Sum {
  categoryName: string;
  categoryValue: number;
}

export interface TransformedSum {
  overallSum: number;
  sum: Sum[]
}
