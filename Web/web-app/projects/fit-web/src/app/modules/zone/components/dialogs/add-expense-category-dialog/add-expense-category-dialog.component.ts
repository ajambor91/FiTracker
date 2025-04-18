import {ChangeDetectionStrategy, Component} from '@angular/core';
import {FormGroup} from '@angular/forms';
import {forkJoin, map, of, switchMap} from 'rxjs';
import {ActivatedRoute, Router} from '@angular/router';
import {addExpenseCategoryForm, AddExpenseCategoryForm} from '../../../forms/add-expense-category.form';
import {ExpensesService} from '../../../services/expenses.service';
import {NavService} from '../../../services/nav.service';

@Component({
  selector: 'app-add-expense',
  standalone: false,
  templateUrl: './add-expense-category-dialog.component.html',
  styleUrl: './add-expense-category-dialog.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class AddExpenseCategoryDialogComponent {

  private _form: FormGroup<AddExpenseCategoryForm> = addExpenseCategoryForm;

  public get form(): FormGroup<AddExpenseCategoryForm> {
    return this._form as FormGroup<AddExpenseCategoryForm>;
  }
  constructor(private expenseService: ExpensesService, private router: Router, private activatedRoute: ActivatedRoute, private navService: NavService) {
  }

  public submitForm(): void {
    this.activatedRoute.params.pipe(
      map(param => param["id"]),
      switchMap(id => forkJoin([of(id), this.expenseService.addExpenseCategory(id, this.form)]))
    ).subscribe(([id]) => {
      this.navService.closeDialog(id)
    });
  }


}
