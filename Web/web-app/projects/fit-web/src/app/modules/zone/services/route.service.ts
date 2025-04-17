import {Injectable} from '@angular/core';
import {BehaviorSubject, map, Observable, share, tap} from 'rxjs';


@Injectable()
export class RouteService {
  private _currentRoute: BehaviorSubject<string[]> = new BehaviorSubject<string[]>([])

  public get currentRoute$(): Observable<string[]> {
    return this._currentRoute.asObservable().pipe(share());
  }

  public get onOverview$(): Observable<boolean> {
    return this.currentRoute$.pipe(
      map((routes: string[]) => undefined != routes.find(route => route === 'overview')),
      share())
  }

  public setCurrentSegment(url: string[]): void {
    this._currentRoute.next(url);
  }


}
