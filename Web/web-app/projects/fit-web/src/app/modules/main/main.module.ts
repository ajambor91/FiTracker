import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {MainContainerComponent} from './components/main-container/main-container.component';
import {MainRoutingModule} from './main-routing.module';
import {NavbarComponent} from './components/navbar/navbar.component';
import {MainComponent} from './components/main-component/main.component';
import {SharedModule} from '../shared/shared.module';


@NgModule({
  declarations: [
    MainContainerComponent,
    NavbarComponent,
    MainComponent
  ],
  imports: [
    MainRoutingModule,
    CommonModule,
    SharedModule
  ],
  exports: [
    MainContainerComponent
  ]
})
export class MainModule {
}
