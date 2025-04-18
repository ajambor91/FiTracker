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

  constructor(private navService: NavService) {
  }

  public ngOnInit(): void {
    this.isLoaded$ = this.navService.isLoaded$;
  }
}
