import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {DashboardComponent} from './components/dashboard/dashboard.component';
import {MainContainerComponent} from './components/main-container/main-container.component';
import {ZoneRoutingModule} from './zone-routing.module';
import {SharedModule} from '../shared/shared.module';


@NgModule({
  declarations: [
    DashboardComponent,
    MainContainerComponent,
  ],
  imports: [
    SharedModule,
    ZoneRoutingModule,
    CommonModule
  ]
})
export class ZoneModule {
}
