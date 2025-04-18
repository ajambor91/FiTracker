import {AfterViewInit, Component, ElementRef, ViewChild} from '@angular/core';
import {ZoneService} from '../../services/zone.service';
import {distinctUntilChanged, map, merge, Observable, Subject, switchMap, takeUntil, tap} from 'rxjs';
import {ActivatedRoute} from '@angular/router';
import {InitialZone} from '../../models/initial-zone.model';
import Chart, {ChartTypeRegistry} from 'chart.js/auto';
import {TotalSummaryByCategory, TotalSummaryByDate} from 'api';
import {DestroyableAbstract} from '../../../shared/components/abstract/destroyable.abstract';

@Component({
  selector: 'app-zone',
  standalone: false,
  templateUrl: './zone.component.html',
  styleUrl: './zone.component.scss'
})
export class ZoneComponent extends DestroyableAbstract implements AfterViewInit {
  public zone$!: Observable<InitialZone | null>;
  @ViewChild('chartByCategory') private chartByCategory!: ElementRef;
  @ViewChild('chartByDate') private chartByDate!: ElementRef;
  private categoriesChart!: Chart<"bar" | "line" | "scatter" | "bubble" | "pie" | "doughnut" | "polarArea" | "radar", [ChartTypeRegistry["bar" | "line" | "scatter" | "bubble" | "pie" | "doughnut" | "polarArea" | "radar"]["defaultDataPoint"]] extends [unknown] ? Array<ChartTypeRegistry["bar" | "line" | "scatter" | "bubble" | "pie" | "doughnut" | "polarArea" | "radar"]["defaultDataPoint"]> : never, any>;
  private dateChart!: Chart<"bar" | "line" | "scatter" | "bubble" | "pie" | "doughnut" | "polarArea" | "radar", [ChartTypeRegistry["bar" | "line" | "scatter" | "bubble" | "pie" | "doughnut" | "polarArea" | "radar"]["defaultDataPoint"]] extends [unknown] ? Array<ChartTypeRegistry["bar" | "line" | "scatter" | "bubble" | "pie" | "doughnut" | "polarArea" | "radar"]["defaultDataPoint"]> : never, any>;
  private today: Date = new Date();
  private dateSubject: Subject<Date | null> = new Subject<Date | null>();
  private selectedDate: Date = this.today;
  public get currentMonth(): number {
    return this.selectedDate.getMonth();
  }

  public get currentYear(): number {
    return this.selectedDate.getFullYear();
  }

  private get date$(): Observable<Date | null> {
    return this.dateSubject.asObservable();
  }
  constructor(private zonesService: ZoneService, private route: ActivatedRoute) {
    super();
  }



  public ngAfterViewInit(): void {
    this.iniCharts();
    const paramsOrDate$ = merge(
      this.route.params.pipe(
        map(params => ({type: 'id', value: params['id']})),
        distinctUntilChanged()
      ),
      this.date$.pipe(
        map(date => ({type: 'date', value: date})),
        distinctUntilChanged()
      )
    );
    this.zone$ = paramsOrDate$.pipe(
      takeUntil(this.destroy$),
      switchMap((param) => {
        let id: string | undefined = undefined;
        let date: Date | undefined = undefined;
        if (param.type === 'id') {
          id = param.value;
          date = this.today;
        } else if (param.type === 'date') {
          date = param.value;
          id = this.route.snapshot.params['id'];
        }
        return this.zonesService.getInitialZoneData(id as string, date)
      }),
      tap(data => {
        this.updateCategoriesChart(data?.byCategory as TotalSummaryByCategory[])
        this.updateDateChart(data?.byDate as TotalSummaryByDate[])
      }),
    );
  }

  public setNewDateMonthly(month: number): void {
    let monthToSet: number = month;
    if (month < 0) {
      monthToSet = 11;
      this.selectedDate.setFullYear(this.currentYear - 1);

    } else if (month > 11) {
      monthToSet = 0;
      this.selectedDate.setFullYear(this.currentYear + 1);
    }
    this.selectedDate.setMonth(monthToSet);
    this.dateSubject.next(this.selectedDate);
  }

  private iniCharts(): void {
    this.initCategoryChart();
    this.initDateChart();
  }

  private initCategoryChart(): void {
    const canvas = this.chartByCategory.nativeElement;
    const ctx = canvas.getContext('2d');
    this.categoriesChart = new Chart(ctx, {
      type: 'pie',
      data: {
        labels: [],
        datasets: []
      },
    });
  }

  private initDateChart(): void {
    const canvas = this.chartByDate.nativeElement;
    const ctx = canvas.getContext('2d');
    this.dateChart = new Chart(ctx, {
      type: 'bar',
      data: {
        labels: [],
        datasets: []
      },
      options: {
        maintainAspectRatio: false,
      },
    });
  }

  private updateDateChart(data: TotalSummaryByDate[]): void {
    this.dateChart?.data.datasets.forEach(() => this.dateChart?.data.datasets.pop());
    this.dateChart?.data.labels?.forEach(() => this.dateChart?.data.labels?.pop());

    this.dateChart?.data.labels?.push(...data.map(expense => expense.date));
    this.dateChart?.data.datasets.push({
      label: "Expenses by category",
      data: data.map(expense => +expense.expensesValue),
      type: 'bar'
    });
    this.dateChart?.update();
  }

  private updateCategoriesChart(data: TotalSummaryByCategory[]) {

    this.categoriesChart?.data.datasets.forEach(() => this.categoriesChart?.data.datasets.pop());
    this.categoriesChart?.data.labels?.forEach(() => this.categoriesChart?.data.labels?.pop());

    this.categoriesChart?.data.labels?.push(...data.map(expense => expense.categoryName));
    this.categoriesChart?.data.datasets.push({
      label: "Expenses by category",
      data: data.map(expense => +expense.expensesValue),
      type: 'pie'
    });
    this.categoriesChart?.update();
  }

  private generateRandomColor(): string {
    const r = Math.floor(Math.random() * 255);
    const g = Math.floor(Math.random() * 255);
    const b = Math.floor(Math.random() * 255);
    return `rgba(${r}, ${g}, ${b}, 0.7)`;
  }
}
