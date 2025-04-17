import {Injectable, OnDestroy} from '@angular/core';
import {Observable, Subject} from 'rxjs';

@Injectable()
export class DestroyableAbstract implements OnDestroy {
  private readonly _destroy: Subject<void> = new Subject<void>();

  public get destroy$(): Observable<void> {
    return this._destroy;
  }

  public ngOnDestroy(): void {
    this._destroy.next();
  }
}
