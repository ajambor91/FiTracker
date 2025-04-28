import {NgModule} from '@angular/core';
import {AuthComponent} from './components/auth-component/auth.component';

import {SharedModule} from '../shared/shared.module';
import {UsersService} from './services/users.service';
import {ApiUsersService} from 'api';
import {UsersRoutingModule} from './users-routing.module';
import {MainContainerComponent} from './components/main-container/main-container.component';
import {ReactiveFormsModule} from '@angular/forms';
import {RegisterComponent} from './components/register-component/register.component';
import {RegisterSuccessComponent} from './components/register-success/register-success.component';
import {AccountComponent} from './components/account-component/account.component';
import {EditAccountComponent} from './components/edit-account-component/edit-account.component';
import {RemoveAccountComponent} from './components/remove-account-component/remove-account.component';
import {HomeAccountComponent} from './components/home-account-component/home-account.component';

@NgModule({
  declarations: [
    AuthComponent,
    MainContainerComponent,
    RegisterComponent,
    RegisterSuccessComponent,
    AccountComponent,
    EditAccountComponent,
    RemoveAccountComponent,
    HomeAccountComponent
  ],
  imports: [
    ReactiveFormsModule,
    UsersRoutingModule,
    SharedModule

  ],
  providers: [
    ApiUsersService,
    UsersService,
  ]
})
export class UsersModule {
}
