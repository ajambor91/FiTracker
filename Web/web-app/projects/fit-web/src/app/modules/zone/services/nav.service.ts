import {Injectable} from '@angular/core';
import {MatDialog} from '@angular/material/dialog';

import {DialogContainerComponent} from '../components/dialogs/dialog-container/dialog-container.component';
import {Router} from '@angular/router';
import {BehaviorSubject, filter, Observable, Subscription, take} from 'rxjs';
import {RouteService} from './route.service';


@Injectable()
export class NavService {
  private _isLoaded: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  private _currentDialog!: Subscription;
  public get isLoaded$(): Observable<boolean> {
    return this._isLoaded.asObservable();
  }
  constructor(public dialog: MatDialog, private router: Router, private routeService: RouteService) {
  }



  public openCategoryDialog(routes: string[]): void {
    this._currentDialog = this.dialog.open(
      DialogContainerComponent,
      {
        width: '740px',
        maxWidth: '740px',
        minWidth: '740px',
        maxHeight: '700px',
        disableClose: true,
        enterAnimationDuration: '.1s',
        panelClass: 'modal-form'
      }
    ).afterOpened().subscribe(() => {
      this.router.navigate(['dashboard', {outlets: {dialog: routes}}]);
      this._isLoaded.next(true)
    })
  }

  public closeDialog(id: string): void {
    this._isLoaded.next(false);
    this.isLoaded$.pipe(
      filter(isLoaded => !isLoaded),
      take(1)).subscribe(() => {
      this.goTo(id)
      this._currentDialog.unsubscribe();
      this.dialog.closeAll();

    })

  }

  private goTo(id: string): void {
    this.router.navigate(['dashboard', {outlets: {dialog: []}}])
  }


}
