import {Component, EventEmitter, Input, Output} from '@angular/core';
import {Months} from '../../enums/months.enum';
import {faChevronLeft, faChevronRight} from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'app-month-picker',
  standalone: false,
  templateUrl: './month-picker.component.html',
  styleUrl: './month-picker.component.scss'
})
export class MonthPickerComponent {
  @Input('currentYear') currentYear!: number;
  @Input('currentMonth') currentMonth!: number;
  @Output('changeMonth') changeMonth: EventEmitter<number> = new EventEmitter<number>();
  public arrowRight = faChevronRight;
  public arrowLeft = faChevronLeft;
  protected readonly faChevronLeft = faChevronLeft;
  private _months: Months[] = [
    Months.JAN, Months.FEB, Months.MAR, Months.APR, Months.MAY, Months.JUN,
    Months.JUL, Months.AUG, Months.SEP, Months.OCT, Months.NOV, Months.DEC
  ];

  public get getPrevMonth(): Months {
    let monthToSet: number = this.currentMonth - 1;
    if (monthToSet < 0) {
      monthToSet = 11
    }
    return this._months[monthToSet];
  }

  public get getNextMonth(): Months {
    let monthToSet: number = this.currentMonth + 1;
    if (monthToSet > 11) {
      monthToSet = 0
    }
    return this._months[monthToSet];
  }

  public get getCurrentMonth(): Months {
    return this._months[this.currentMonth];
  }
}
