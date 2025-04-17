import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {DashboardComponent} from './components/dashboard/dashboard.component';
import {MainContainerComponent} from './components/main-container/main-container.component';
import {ZoneRoutingModule} from './zone-routing.module';
import {SharedModule} from '../shared/shared.module';
import {MatTab, MatTabGroup, MatTabLink, MatTabNav, MatTabNavPanel} from "@angular/material/tabs";
import {NewZoneComponent} from './components/new-zone/new-zone.component';
import {ZoneService} from './services/zone.service';
import {ApiUsersService, CategoriesApiService, ExpensesApiService, ZoneApiService} from 'api';
import {AddMembersComponent} from './components/add-members/add-members.component';
import {MembersService} from './services/members.service';
import {EffectsModule} from '@ngrx/effects';
import {StoreModule} from '@ngrx/store';
import {zoneReducer} from './store/zone.reducer';
import {zonesEffects} from './store/zone.effects';
import {DashboardNavComponent} from './components/dashboard-nav/dashboard-nav.component';
import {ZoneComponent} from './components/zone/zone.component';
import {ExpensesService} from './services/expenses.service';
import {AddExpenseComponent} from './components/add-expense/add-expense.component';
import {NavService} from './services/nav.service';
import {UpdateZoneDialogComponent} from './components/dialogs/update-zone-dialog/update-zone-dialog.component';
import {DialogContainerComponent} from './components/dialogs/dialog-container/dialog-container.component';
import {AddExpenseCategoryComponent} from './components/add-expense-category/add-expense-category.component';
import {AddExpenseDialogComponent} from './components/dialogs/add-expense-dialog/add-expense-dialog.component';
import {
  AddExpenseCategoryDialogComponent
} from './components/dialogs/add-expense-category-dialog/add-expense-category-dialog.component';
import {MatDialogModule} from '@angular/material/dialog';
import {RouteService} from './services/route.service';
import {MatOption} from '@angular/material/core';
import {MatSelect} from '@angular/material/select';
import {DialogContentComponent} from './components/dialogs/dialog-content/dialog-content.component';
import {AllZonesComponent} from './components/all-zones/all-zones.component';
import {MatList, MatListItem} from "@angular/material/list";

@NgModule({
  declarations: [
    AddExpenseDialogComponent,
    AddExpenseCategoryDialogComponent,
    AddExpenseCategoryComponent,
    AddExpenseComponent,
    DashboardComponent,
    MainContainerComponent,
    NewZoneComponent,
    AddMembersComponent,
    DashboardNavComponent,
    UpdateZoneDialogComponent,
    ZoneComponent,
    DialogContentComponent,
    DialogContainerComponent,
    AllZonesComponent
  ],
  imports: [
    MatDialogModule,
    SharedModule,
    ZoneRoutingModule,
    CommonModule,
    MatTabGroup,
    MatTab,
    EffectsModule.forFeature([zonesEffects]),
    StoreModule.forFeature('zones', zoneReducer),
    MatTabNavPanel,
    MatTabNav,
    MatSelect,
    MatOption,
    MatTabLink,
    MatList,
    MatListItem,
  ],
  providers: [
    RouteService,
    NavService,
    ZoneService,
    ApiUsersService,
    ZoneApiService,
    MembersService,
    ExpensesApiService,
    ExpensesService,
    CategoriesApiService
  ]
})
export class ZoneModule {
}
