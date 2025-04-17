import {FormControl, FormGroup} from '@angular/forms';


export interface AddExpenseCategoryForm {
  name: FormControl<string>;
  description: FormControl<string | null>;
}

export const addExpenseCategoryForm: FormGroup<AddExpenseCategoryForm> = new FormGroup<AddExpenseCategoryForm>({
  name: new FormControl('', {nonNullable: true}),
  description: new FormControl('', {nonNullable: false})
})
