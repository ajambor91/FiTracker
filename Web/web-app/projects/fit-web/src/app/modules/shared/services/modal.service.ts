import {Injectable} from '@angular/core';
import {MatDialog, MatDialogRef} from '@angular/material/dialog';
import {Observable} from 'rxjs';
import {ConfirmComponent} from '../components/dialogs/confirm/confirm.component';

@Injectable()
export class ModalService {

  constructor(private dialog: MatDialog) {
  }

  public openDialog(data: string): Observable<any> {
    const dialogRef: MatDialogRef<any> = this.dialog.open(ConfirmComponent, {
      width: '200px',
      data: data
    });
    return dialogRef.afterClosed();
  }
}
