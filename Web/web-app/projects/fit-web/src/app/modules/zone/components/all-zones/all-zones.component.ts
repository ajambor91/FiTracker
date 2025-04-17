import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {ZoneService} from '../../services/zone.service';
import {map, Observable} from 'rxjs';
import {ExpensesService} from '../../services/expenses.service';
import {Zone} from '../../models/zone.model';
import {ActivatedRoute, Router} from '@angular/router';

@Component({
  selector: 'app-all-zones',
  standalone: false,
  templateUrl: './all-zones.component.html',
  styleUrl: './all-zones.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class AllZonesComponent implements OnInit {

  public zones$!: Observable<Zone[]>;


  constructor(
    private zoneService: ZoneService,
    private activatedRoute: ActivatedRoute,
    private expensesService: ExpensesService,
    private router: Router) {
  }

  public ngOnInit(): void {
    this.zones$ = this.zoneService.getAllUserZonesFromApi().pipe(map(zones => zones.zones));
  }

  public show(zoneId: string): void {
    this.router.navigate(['dashboard', 'zones', 'overview', zoneId])
  }

}
