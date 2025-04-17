import {FormControl, FormGroup} from '@angular/forms';


export interface AddExpenseForm {
  name: FormControl<string>;
  value: FormControl<number>;
}

export interface AddExpenseMultiCategoriesForm extends AddExpenseForm {
  categoriesIds: FormControl<string[]>
}

export const addExpenseForm: FormGroup<AddExpenseForm> = new FormGroup<AddExpenseForm>({
  name: new FormControl('', {nonNullable: true}),
  value: new FormControl(0, {nonNullable: true})
})
export const addExpenseMultiCategoriesForm: FormGroup<AddExpenseMultiCategoriesForm> = new FormGroup<AddExpenseMultiCategoriesForm>({
  name: new FormControl('', {nonNullable: true}),
  value: new FormControl(0, {nonNullable: true}),
  categoriesIds: new FormControl()
})
