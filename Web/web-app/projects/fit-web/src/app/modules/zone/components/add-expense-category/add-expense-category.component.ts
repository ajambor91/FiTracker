import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {FormGroup} from '@angular/forms';

import {forkJoin, map, of, switchMap} from 'rxjs';
import {ActivatedRoute, Router} from '@angular/router';
import {addExpenseCategoryForm, AddExpenseCategoryForm} from '../../forms/add-expense-category.form';
import {ExpensesService} from '../../services/expenses.service';

@Component({
  selector: 'app-add-expense-category',
  standalone: false,
  templateUrl: './add-expense-category.component.html',
  styleUrl: './add-expense-category.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class AddExpenseCategoryComponent {
  private _form: FormGroup<AddExpenseCategoryForm> = addExpenseCategoryForm;
  public get form(): FormGroup<AddExpenseCategoryForm> {
    return this._form as FormGroup<AddExpenseCategoryForm>;
  }
  constructor(private expenseService: ExpensesService, private router: Router, private activatedRoute: ActivatedRoute) {
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
