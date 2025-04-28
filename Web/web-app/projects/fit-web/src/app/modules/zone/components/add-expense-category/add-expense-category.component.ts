import {ChangeDetectionStrategy, Component} from '@angular/core';
import {AbstractControl, FormGroup} from '@angular/forms';

import {forkJoin, map, of, switchMap} from 'rxjs';
import {ActivatedRoute, Router} from '@angular/router';
import {addExpenseCategoryForm, AddExpenseCategoryForm} from '../../forms/add-expense-category.form';
import {ExpensesService} from '../../services/expenses.service';
import {SnackbarService} from '../../../shared/services/snackbar.service';

@Component({
  selector: 'app-add-expense-category',
  standalone: false,
  templateUrl: './add-expense-category.component.html',
  styleUrl: './add-expense-category.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class AddExpenseCategoryComponent {
  constructor(
    private expenseService: ExpensesService,
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private snackbar: SnackbarService
  ) {
  }

  private _form: FormGroup<AddExpenseCategoryForm> = addExpenseCategoryForm;

  public get form(): FormGroup<AddExpenseCategoryForm> {
    return this._form as FormGroup<AddExpenseCategoryForm>;
  }

  public get isNameInvalid(): boolean {
    const nameControl: AbstractControl = this._form.get('name') as AbstractControl;
    return nameControl.invalid && nameControl.touched;
  }

  public get isDescriptionInvalid(): boolean {
    const descriptionControl: AbstractControl = this._form.get('description') as AbstractControl;
    return descriptionControl.invalid && descriptionControl.touched;
  }

  public skip(): void {
    this.activatedRoute.params.pipe(
      map(param => param["id"]),
    ).subscribe((id) => {
      this.router.navigate(['dashboard', 'zones', 'overview', id]);
    });
  }

  public submitForm(): void {
    this.activatedRoute.params.pipe(
      map(param => param["id"]),
      switchMap(id => forkJoin([of(id), this.expenseService.addExpenseCategory(id, this.form)]))
    ).subscribe(([id, category]) => {
      this.router.navigate(['dashboard', 'zones', id, 'category', category.categoryId, 'expense', 'add']);
    });


  }


}
