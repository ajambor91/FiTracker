import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {AuthComponent} from './components/auth-component/auth.component';
import {RegisterComponent} from './components/register-component/register.component';
import {RegisterSuccessComponent} from './components/register-success/register-success.component';
import {authGuard} from '../../core/guards/auth.guard';
import {AccountComponent} from './components/account-component/account.component';
import {EditAccountComponent} from './components/edit-account-component/edit-account.component';
import {RemoveAccountComponent} from './components/remove-account-component/remove-account.component';
import {HomeAccountComponent} from './components/home-account-component/home-account.component';

const routes: Routes = [
  {
    path: 'login',
    component: AuthComponent
  },
  {
    path: 'register',
    component: RegisterComponent
  },
  {
    path: 'register/success',
    component: RegisterSuccessComponent
  },
  {
    path: 'account',
    component: AccountComponent,
    canActivate: [authGuard],
    children: [
      {
        path: 'edit',
        component: EditAccountComponent
      },
      {
        path: 'remove',
        component: RemoveAccountComponent
      },
      {
        path: '',
        component: HomeAccountComponent
      }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class UsersRoutingModule {
}
