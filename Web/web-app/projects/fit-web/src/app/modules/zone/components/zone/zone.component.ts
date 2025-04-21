import {AfterViewInit, Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {ZoneService} from '../../services/zone.service';
import {
  distinctUntilChanged,
  EMPTY,
  filter,
  map,
  merge,
  Observable,
  shareReplay,
  Subject,
  switchMap,
  take,
  takeUntil,
  tap
} from 'rxjs';
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
export class ZoneComponent extends DestroyableAbstract implements OnInit, AfterViewInit {
  public zoneId$!: Observable<string>;
  public zone$!: Observable<InitialZone | null>;
  private lastZoneId!: string;
  private lastDate!: Date;
  private categoriesChart!: Chart<"bar" | "line" | "scatter" | "bubble" | "pie" | "doughnut" | "polarArea" | "radar", [ChartTypeRegistry["bar" | "line" | "scatter" | "bubble" | "pie" | "doughnut" | "polarArea" | "radar"]["defaultDataPoint"]] extends [unknown] ? Array<ChartTypeRegistry["bar" | "line" | "scatter" | "bubble" | "pie" | "doughnut" | "polarArea" | "radar"]["defaultDataPoint"]> : never, any>;
  private dateChart!: Chart<"bar" | "line" | "scatter" | "bubble" | "pie" | "doughnut" | "polarArea" | "radar", [ChartTypeRegistry["bar" | "line" | "scatter" | "bubble" | "pie" | "doughnut" | "polarArea" | "radar"]["defaultDataPoint"]] extends [unknown] ? Array<ChartTypeRegistry["bar" | "line" | "scatter" | "bubble" | "pie" | "doughnut" | "polarArea" | "radar"]["defaultDataPoint"]> : never, any>;
  private today: Date = new Date();
  private dateSubject: Subject<Date | null> = new Subject<Date | null>();
  private selectedDate: Date = this.today;

  constructor(private zonesService: ZoneService, private route: ActivatedRoute) {
    super();
  }

  public get currentMonth(): number {
    return this.selectedDate.getMonth();
  }

  public get currentYear(): number {
    return this.selectedDate.getFullYear();
  }

  private _chartByCategory!: ElementRef;

  private get chartByCategory(): ElementRef {
    return this._chartByCategory;
  }

  @ViewChild('chartByCategory')
  private set chartByCategory(el: ElementRef) {
    this._chartByCategory = el;
    this.initCategoryChart();
  }

  private _chartByDate!: ElementRef;

  private get chartByDate(): ElementRef {
    return this._chartByDate;
  }

  @ViewChild('chartByDate')
  private set chartByDate(el: ElementRef) {
    this._chartByDate = el;
    this.initDateChart();
  }

  private get date$(): Observable<Date | null> {
    return this.dateSubject.asObservable();
  }

  public ngOnInit(): void {
    this.zoneId$ = this.route.params.pipe(map(param => param["id"]));
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

        if (date?.getTime() === this.lastDate?.getTime() && id === this.lastZoneId) {
          return EMPTY;
        }

        this.lastZoneId = id as string;
        this.lastDate = new Date(date as Date);
        return this.zonesService.getInitialZoneData(id as string, date)
      }),
      tap(data => {
        this.updateCategoriesChart(data?.byCategory as TotalSummaryByCategory[])
        this.updateDateChart(data?.byDate as TotalSummaryByDate[])
      }),
      shareReplay()
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
    if (this.chartByCategory === undefined || this.chartByCategory == null) {
      return;
    }
    this.zone$.pipe(
      filter(zone => !!zone),
      take(1),
      map(data => data.byCategory)
    ).subscribe(charteData => {
      const canvas = this.chartByCategory?.nativeElement;
      const ctx = canvas.getContext('2d');
      this.categoriesChart = new Chart(ctx, {
        type: 'pie',
        data: {
          labels: charteData.map(chart => chart.categoryName),
          datasets: [this.updateCategoriesChartDataset(charteData)
          ]
        },
      });
    })

  }

  private initDateChart(): void {
    if (this.chartByDate === undefined || this.chartByDate == null) {
      return;
    }
    this.zone$.pipe(
      take(1),
      filter(data => !!data),
      map((zone: InitialZone) => zone.byDate)).subscribe(charteData => {
      const canvas = this.chartByDate?.nativeElement;
      const ctx = canvas.getContext('2d');
      this.dateChart = new Chart(ctx, {
        type: 'bar',
        data: {
          labels: charteData.map(chart => chart.date),
          datasets: [this.updateDateChartDataset(charteData)]
        }
        ,
        options: {
          maintainAspectRatio: false,
        },
      });
    })

  }

  private updateDateChartDataset(charteData: TotalSummaryByDate[]): any {
    let color: string;
    let currentChart: TotalSummaryByDate;
    return {
      data: charteData.map(item => {
        currentChart = item;
        color = this.generateRandomColor()
        return item.expensesValue
      }),
      label: 'Expenses',
      backgroundColor: charteData.map(() => color),
      borderColor: charteData.map(() => color),
      borderWidth: 1,
    }
  }

  private updateCategoriesChartDataset(charteData: TotalSummaryByCategory[]): any {
    let color: string;
    return {
      data: charteData.map(item => item.expensesValue),
      backgroundColor: charteData.map(() => {
        const generatedColor: string = this.generateRandomColor();
        color = generatedColor;
        return generatedColor
      }),
      borderColor: charteData.map(() => color),
      borderWidth: 1,
    }

  }

  private updateDateChart(data: TotalSummaryByDate[]): void {
    this.dateChart?.data.datasets.forEach(() => this.dateChart?.data.datasets.pop());
    this.dateChart?.data.labels?.forEach(() => this.dateChart?.data.labels?.pop());

    this.dateChart?.data.labels?.push(...data.map(expense => expense.date));
    this.dateChart?.data.datasets.push(this.updateDateChartDataset(data));
    this.dateChart?.update();
  }

  private updateCategoriesChart(data: TotalSummaryByCategory[]) {

    if (this.categoriesChart?.data?.labels && this.categoriesChart?.data?.datasets) {
      const datasetsLen: number = this.categoriesChart.data.datasets.length;
      for (let i = 0; i < datasetsLen; i++) {
        this.categoriesChart.data.datasets.pop();
      }
      const labelLen: number = this.categoriesChart.data.labels.length;
      for (let i = 0; i < labelLen; i++) {
        this.categoriesChart.data.labels.pop();
      }
    }

    this.categoriesChart?.data.labels?.push(...data.map(expense => expense.categoryName));
    this.categoriesChart?.data.datasets.push(this.updateCategoriesChartDataset(data));
    this.categoriesChart?.update();
  }

  private generateRandomColor(): string {
    const r = Math.floor(Math.random() * 255);
    const g = Math.floor(Math.random() * 255);
    const b = Math.floor(Math.random() * 255);
    return `rgba(${r}, ${g}, ${b}, 0.7)`;
  }
}
