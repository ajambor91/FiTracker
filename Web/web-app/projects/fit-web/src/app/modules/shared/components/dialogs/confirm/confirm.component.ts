import {Component, inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';


@Component({
  selector: 'app-header',
  standalone: false,
  templateUrl: './confirm.component.html',
  styleUrl: './confirm.component.scss'
})
export class ConfirmComponent {
  public data: string = inject(MAT_DIALOG_DATA);

  constructor(private dialogRef: MatDialogRef<any>) {
  }

  public accept(): void {
    this.dialogRef.close(true)
  }

  public decline(): void {
    this.dialogRef.close(false);
  }

}
