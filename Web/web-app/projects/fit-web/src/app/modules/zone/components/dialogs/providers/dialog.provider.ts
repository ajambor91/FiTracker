import {Provider} from '@angular/core';
import {NavService} from '../../../services/nav.service';
import {ExpensesService} from '../../../services/expenses.service';
import {ApiUsersService, CategoriesApiService, ExpensesApiService} from 'api';
import {MembersService} from '../../../services/members.service';
import {ZoneService} from '../../../services/zone.service';

export const dialogProvider: Provider[] = [
  {
    provide: ZoneService,
    useClass: ZoneService
  },
  {
    provide: NavService,
    useClass: NavService
  },
  {
    provide: ApiUsersService,
    useClass: ApiUsersService
  },
  {
    provide: MembersService,
    useClass: MembersService
  },
  {
    provide: CategoriesApiService,
    useClass: CategoriesApiService
  },
  {
    provide: ExpensesService,
    useClass: ExpensesService
  },
  {
    provide: ExpensesApiService,
    useClass: ExpensesApiService
  }

]
