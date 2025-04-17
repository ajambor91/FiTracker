import {Component, OnInit} from '@angular/core';
import {NavService} from '../../../services/nav.service';
import {filter, map, Observable, take} from 'rxjs';
import {ZoneService} from '../../../services/zone.service';
import {faXmark} from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'app-dialog-container',
  standalone: false,
  templateUrl: './dialog-container.component.html',
  styleUrl: './dialog-container.component.scss',


})
export class DialogContainerComponent implements OnInit {
  public faXmark = faXmark;
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
