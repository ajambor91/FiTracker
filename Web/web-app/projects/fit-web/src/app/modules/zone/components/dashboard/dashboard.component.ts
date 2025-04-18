import {AfterViewInit, Component, OnInit} from '@angular/core';
import {ZoneService} from '../../services/zone.service';
import {filter, Observable, switchMap, tap} from 'rxjs';
import {Zone} from '../../models/zone.model';
import {RouteService} from '../../services/route.service';
import {ActivatedRoute} from '@angular/router';

@Component({
  selector: 'app-dashboard',
  standalone: false,
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})
export class DashboardComponent implements OnInit {
  public zones$!: Observable<Zone[]>;
  public activeZoneId!: string;

  constructor(private zonesService: ZoneService, private activatedRoute: ActivatedRoute, private routeService: RouteService) {
  }
  public ngOnInit(): void {
    this.zonesService.fetchAllUserZones();
    this.zones$ = this.routeService.onOverview$.pipe(
      filter(zone => !zone),
      switchMap(() => this.zonesService.getAllUserZonesOnInit()));

  }

  public selectCurrentZoneAndGo(zone: Zone) {
    this.activeZoneId = zone.zoneId;
    this.zonesService.selectCurrentZoneAndGo(zone)
  }
}
