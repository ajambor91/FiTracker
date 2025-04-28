import {Injectable} from '@angular/core';
import {MatSnackBar} from '@angular/material/snack-bar';


@Injectable()
export class SnackbarService {
  private readonly ACCEPT_ACTION: string = 'Ok';

  constructor(private snackbar: MatSnackBar) {
  }

  public showError(message: string): void {
    this.snackbar.open(message, this.ACCEPT_ACTION, {
      verticalPosition: "bottom",
      panelClass: 'snackbar-background',
      duration: 4000
    })
  }

  public showSuccess(message: string): void {
    this.snackbar.open(message, this.ACCEPT_ACTION, {
      verticalPosition: "bottom",
      panelClass: 'snackbar-background',
      duration: 4000
    })
  }


}
