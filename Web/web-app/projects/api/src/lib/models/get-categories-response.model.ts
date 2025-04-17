export interface Category {
  categoryId: number;
  name: string;
  description?: string;
}

export interface GetCategoriesResponse {
  categories: Category[];
}
