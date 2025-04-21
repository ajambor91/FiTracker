import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {FormControl, FormGroup} from '@angular/forms';
import {forkJoin, map, Observable, of, switchMap} from 'rxjs';
import {ActivatedRoute, Router} from '@angular/router';
import {AddExpenseMultiCategoriesForm, addExpenseMultiCategoriesForm} from '../../../forms/add-expense.form';
import {Category} from '../../../models/category.model';
import {ExpensesService} from '../../../services/expenses.service';
import {ZoneService} from '../../../services/zone.service';
import {GetCategoriesResponse} from '../../../../../../../../api/src/lib/models/get-categories-response.model';
import {NavService} from '../../../services/nav.service';
import {SnackbarService} from '../../../../shared/services/snackbar.service';

@Component({
  selector: 'app-add-expense',
  standalone: false,
  templateUrl: './add-expense-dialog.component.html',
  styleUrl: './add-expense-dialog.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class AddExpenseDialogComponent implements OnInit {
  public categories$!: Observable<Category[]>;

  constructor(
    private zoneService: ZoneService,
    private activatedRoute: ActivatedRoute,
    private expensesService: ExpensesService,
    private router: Router,
    private navService: NavService,
    private snackbar: SnackbarService) {
  }

  private _form: FormGroup<AddExpenseMultiCategoriesForm> = addExpenseMultiCategoriesForm;

  public get form(): FormGroup<AddExpenseMultiCategoriesForm> {
    return this._form;
  }

  public get isNameInvalid(): boolean {
    const nameControl: FormControl = this.form.get('name') as FormControl;
    return nameControl.invalid && nameControl.touched;
  }

  public get isValueInvalid(): boolean {
    const valueControl: FormControl = this.form.get('value') as FormControl;
    return valueControl.invalid && valueControl.touched;
  }

  public get isCategoryInvalid(): boolean {
    const categoriesIdsControl: FormControl = this.form.get('categoriesIds') as FormControl;
    return categoriesIdsControl.invalid && categoriesIdsControl.touched;
  }

  public ngOnInit(): void {
    this.categories$ = this.activatedRoute.params.pipe(
      map(params => params['id']),
      switchMap(id => this.expensesService.getCategories(id)),
      map((categoriesRes: GetCategoriesResponse) => categoriesRes.categories)
    );
  }

  public submitForm(): void {
    if (this._form.valid) {
      this.activatedRoute.params.pipe(
        switchMap(param => forkJoin([
          of(param['id']),
          this.expensesService.addExpenseMultiCategories(
            param["id"],
            this.form
          )
        ]))
      ).subscribe(([id]) => this.navService.closeDialog(id))
    } else {
      this._form.markAllAsTouched();
      this._form.markAsDirty()
      this.snackbar.showError("Correct form errors!")
    }
  }


}
