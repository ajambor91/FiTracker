import {Component, OnInit} from '@angular/core';
import {ZoneService} from '../../services/zone.service';
import {MembersService} from '../../services/members.service';
import {Observable} from 'rxjs';
import {Zone} from '../../models/zone.model';
import {NavService} from '../../services/nav.service';
import {RouteService} from '../../services/route.service';

@Component({
  selector: 'app-dashboard-nav',
  standalone: false,
  templateUrl: './dashboard-nav.component.html',
  styleUrl: './dashboard-nav.component.scss'
})
export class DashboardNavComponent implements OnInit {

  public currentZone$!: Observable<Zone | null>
  public onOverview$!: Observable<boolean>;

  constructor(
    private zoneService: ZoneService,
    private membersService: MembersService,
    private navService: NavService,
    private routeService: RouteService) {

  }

  public ngOnInit(): void {
    this.onOverview$ = this.routeService.onOverview$;
    this.currentZone$ = this.zoneService.getCurrentZone();
  }

  public openAddCategoryDialog(routes: string[]): void {
    this.navService.openCategoryDialog(routes);
  }

}
