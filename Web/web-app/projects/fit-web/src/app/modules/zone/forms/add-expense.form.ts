import {FormControl, FormGroup, Validators} from '@angular/forms';


export interface AddExpenseForm {
  name: FormControl<string>;
  value: FormControl<number | null>;
}

export interface AddExpenseMultiCategoriesForm extends AddExpenseForm {
  categoriesIds: FormControl<string[] | null>
}

export const addExpenseForm: FormGroup<AddExpenseForm> = new FormGroup<AddExpenseForm>({
  name: new FormControl('', {
    nonNullable: true, validators: [
      Validators.required,
      Validators.pattern(/^[\w\d ]+$/)
    ]
  }),
  value: new FormControl({value: null, disabled: false}, {
    nonNullable: false, validators: [
      Validators.required,
      Validators.pattern(/^[\w\d ]+$/)
    ]
  })
})
export const addExpenseMultiCategoriesForm: FormGroup<AddExpenseMultiCategoriesForm> = new FormGroup<AddExpenseMultiCategoriesForm>({
  name: new FormControl('', {
    nonNullable: true, validators: [
      Validators.required,
      Validators.pattern(/^[\w\d ]+$/)

    ]
  }),
  value: new FormControl(
    {value: null, disabled: false},
    {
      nonNullable: false, validators: [
        Validators.required,
        Validators.pattern(/^[\w\d ]+$/)
      ]
    }),
  categoriesIds: new FormControl(
    {value: null, disabled: false},
    {
      nonNullable: true, validators: [
        Validators.required
      ]
    }
  )
})
