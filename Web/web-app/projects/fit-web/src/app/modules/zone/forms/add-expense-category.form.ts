import {FormControl, FormGroup, Validators} from '@angular/forms';


export interface AddExpenseCategoryForm {
  name: FormControl<string>;
  description: FormControl<string | null>;
}

export const addExpenseCategoryForm: FormGroup<AddExpenseCategoryForm> = new FormGroup<AddExpenseCategoryForm>({
  name: new FormControl('', {
    nonNullable: true, validators: [
      Validators.required,
      Validators.pattern(/^[\w\d ]+$/)
    ]
  }),
  description: new FormControl('', {
    nonNullable: false, validators: [
      Validators.pattern(/^[\w\d ]+$/)
    ]
  })
})
