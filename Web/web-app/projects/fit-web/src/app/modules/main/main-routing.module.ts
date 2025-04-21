import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {authGuard} from '../../core/guards/auth.guard';
import {MainComponent} from './components/main-component/main.component';

const routes: Routes = [
  {
    path: '',
    component: MainComponent
  },
  {
    path: 'users',
    loadChildren: () => import('./../users/users.module').then(m => m.UsersModule),
  },
  {
    path: 'dashboard',
    loadChildren: () => import('./../zone/zone.module').then(m => m.ZoneModule),
    canActivate: [authGuard]
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class MainRoutingModule {
}
