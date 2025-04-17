import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {DashboardComponent} from './components/dashboard/dashboard.component';
import {NewZoneComponent} from './components/new-zone/new-zone.component';
import {AddMembersComponent} from './components/add-members/add-members.component';
import {MainContainerComponent} from './components/main-container/main-container.component';
import {ZoneComponent} from './components/zone/zone.component';
import {AddExpenseComponent} from './components/add-expense/add-expense.component';
import {UpdateZoneDialogComponent} from './components/dialogs/update-zone-dialog/update-zone-dialog.component';
import {AddExpenseCategoryComponent} from './components/add-expense-category/add-expense-category.component';
import {AddExpenseDialogComponent} from './components/dialogs/add-expense-dialog/add-expense-dialog.component';
import {
  AddExpenseCategoryDialogComponent
} from './components/dialogs/add-expense-category-dialog/add-expense-category-dialog.component';
import {AllZonesComponent} from './components/all-zones/all-zones.component';

const routes: Routes = [
  {
    path: '',
    component: MainContainerComponent,
    children: [
      {
        path: '',
        component: DashboardComponent,
        children: [
          {
            path: 'zones/overview/:id',
            component: ZoneComponent,
          }
        ]
      },
      {
        path: 'zones/add',
        component: NewZoneComponent,
      },
      {
        path: 'zones/:id/members/add',
        component: AddMembersComponent,
      },
      {
        path: 'zones/:id/categories/add',
        component: AddExpenseCategoryComponent
      },
      {
        path: 'zones/:id/category/:categoryId/expense/add',
        component: AddExpenseComponent
      },
      {
        path: 'zones',
        component: AllZonesComponent
      },
    ],

  },
  {
    path: 'dialog',

    children: [
      {
        path: ':id/update',
        component: UpdateZoneDialogComponent,
      },
      {
        path: ':id/expense/add',
        component: AddExpenseDialogComponent,
      },
      {
        path: ':id/categories/add',
        component: AddExpenseCategoryDialogComponent,
      }
    ],
    outlet: 'dialog'
  },


];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ZoneRoutingModule {
}
