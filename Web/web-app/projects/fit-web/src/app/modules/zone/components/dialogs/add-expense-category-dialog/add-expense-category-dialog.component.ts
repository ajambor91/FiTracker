import {ChangeDetectionStrategy, Component} from '@angular/core';
import {FormControl, FormGroup} from '@angular/forms';
import {forkJoin, map, of, switchMap} from 'rxjs';
import {ActivatedRoute, Router} from '@angular/router';
import {addExpenseCategoryForm, AddExpenseCategoryForm} from '../../../forms/add-expense-category.form';
import {ExpensesService} from '../../../services/expenses.service';
import {NavService} from '../../../services/nav.service';
import {SnackbarService} from '../../../../shared/services/snackbar.service';

@Component({
  selector: 'app-add-expense',
  standalone: false,
  templateUrl: './add-expense-category-dialog.component.html',
  styleUrl: './add-expense-category-dialog.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class AddExpenseCategoryDialogComponent {

  constructor(private expenseService: ExpensesService, private router: Router, private activatedRoute: ActivatedRoute, private navService: NavService, private snackbar: SnackbarService) {
  }

  private _form: FormGroup<AddExpenseCategoryForm> = addExpenseCategoryForm;

  public get form(): FormGroup<AddExpenseCategoryForm> {
    return this._form as FormGroup<AddExpenseCategoryForm>;
  }

  public get isNameInvalid(): boolean {
    const nameControl: FormControl = this._form.get('name') as FormControl;
    return nameControl.invalid && nameControl.touched;
  }

  public get isDescriptionInvalid(): boolean {
    const descriptionControl: FormControl = this._form.get('description') as FormControl;

    return descriptionControl.invalid && descriptionControl.dirty;
  }

  public submitForm(): void {
    if (this._form.valid) {
      this.activatedRoute.params.pipe(
        map(param => param["id"]),
        switchMap(id => forkJoin([of(id), this.expenseService.addExpenseCategory(id, this.form)]))
      ).subscribe(([id]) => {
        this.navService.closeDialog(id)
      });
    } else {
      this._form.markAllAsTouched();
      this.snackbar.showError("Form is invalid. Correct errors");
    }

  }


}
