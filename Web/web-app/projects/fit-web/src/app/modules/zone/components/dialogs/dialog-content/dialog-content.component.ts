import {Component, OnInit} from '@angular/core';
import {NavService} from '../../../services/nav.service';
import {filter, map, Observable, take} from 'rxjs';
import {ZoneService} from '../../../services/zone.service';

@Component({
  selector: 'app-dialog-content',
  standalone: false,
  templateUrl: './dialog-content.component.html',
  styleUrl: './dialog-content.component.scss',


})
export class DialogContentComponent implements OnInit {

  public isLoaded$!: Observable<boolean>;

  constructor(private navService: NavService, private zoneService: ZoneService) {
  }

  public ngOnInit(): void {
    this.isLoaded$ = this.navService.isLoaded$;
  }

  public closeDialog(): void {
    this.zoneService.getCurrentZone().pipe(
      map(zone => zone?.zoneId),
      take(1),
      filter(zoneId => !!zoneId)
    ).subscribe(id => this.navService.closeDialog(id as string))
  }

}
