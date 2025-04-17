import {Component, OnInit} from '@angular/core';
import {NavigationEnd, Router} from '@angular/router';
import {RouteService} from '../../services/route.service';
import {filter, takeUntil} from 'rxjs';
import {DestroyableAbstract} from '../../../shared/components/abstract/destroyable.abstract';

@Component({
  selector: 'app-main-container',
  standalone: false,
  templateUrl: './main-container.component.html',
  styleUrl: './main-container.component.scss'
})
export class MainContainerComponent extends DestroyableAbstract implements OnInit {
  constructor(private router: Router, private routeService: RouteService) {
    super();
  }

  ngOnInit(): void {
    this.router.events.pipe(
      filter((event) => event instanceof NavigationEnd),
      takeUntil(this.destroy$)
    ).subscribe((event: NavigationEnd) => {
      const routes: string[] = event.url.split('/');
      this.routeService.setCurrentSegment(routes)
    });
  }
}
