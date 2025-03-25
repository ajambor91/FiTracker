import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {MainContainerComponent} from './components/main-container/main-container.component';
import {MainRoutingModule} from './main-routing.module';
import {NavbarComponent} from './components/navbar/navbar.component';


@NgModule({
  declarations: [
    MainContainerComponent,
    NavbarComponent
  ],
  imports: [
    MainRoutingModule,
    CommonModule
  ],
  exports: [
    MainContainerComponent
  ]
})
export class MainModule {
}
