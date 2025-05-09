import {ChangeDetectionStrategy, Component} from '@angular/core';
import {AbstractControl, FormGroup} from '@angular/forms';
import {ZoneService} from '../../services/zone.service';
import {forkJoin, map, of, switchMap} from 'rxjs';
import {addExpenseForm, AddExpenseForm} from '../../forms/add-expense.form';
import {ExpensesService} from '../../services/expenses.service';
import {ActivatedRoute, Router} from '@angular/router';
import {SnackbarService} from '../../../shared/services/snackbar.service';

@Component({
  selector: 'app-add-expense',
  standalone: false,
  templateUrl: './add-expense.component.html',
  styleUrl: './add-expense.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class AddExpenseComponent {


  constructor(
    private zoneService: ZoneService,
    private activatedRoute: ActivatedRoute,
    private expensesService: ExpensesService,
    private router: Router,
    private snackbar: SnackbarService) {
  }

  private _form: FormGroup<AddExpenseForm> = addExpenseForm;

  public get form(): FormGroup<AddExpenseForm> {
    return this._form;
  }

  public get isNameInvalid(): boolean {
    const nameControl: AbstractControl = this.form.get('name') as AbstractControl;
    return nameControl.invalid && nameControl.touched;
  }

  public get isValueInvalid(): boolean {
    const valueControl: AbstractControl = this.form.get('value') as AbstractControl;
    return valueControl.invalid && valueControl.touched;
  }

  public skip(): void {
    this.activatedRoute.params.pipe(
      map(param => param['id'])
    ).subscribe(id => this.goToDashboard(id))
  }

  public submitForm(): void {
    this.activatedRoute.params.pipe(
      switchMap(param => forkJoin([
        of(param['id']),
        this.expensesService.addExpense(
          param["id"],
          param['categoryId'],
          this.form
        )
      ]))
    ).subscribe(([id]) => this.goToDashboard(id))
  }

  private goToDashboard(id: number): void {
    this.zoneService.fetchAllUserZones();
    this.router.navigate(['dashboard', 'zones', 'overview', id]);
  }

}
