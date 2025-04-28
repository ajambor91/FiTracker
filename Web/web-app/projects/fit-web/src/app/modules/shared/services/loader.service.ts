import {Injectable} from '@angular/core';
import {Overlay, OverlayConfig, OverlayRef} from '@angular/cdk/overlay';
import {ComponentPortal} from '@angular/cdk/portal';
import {defer, map, merge, Observable, scan, Subject} from 'rxjs';
import {LoaderComponent} from '../components/loader/loader.component';


@Injectable()
export class LoaderService {
  private overlayRef: OverlayRef | null = null;
  private loadingRequests = 0;

  private readonly show$ = new Subject<boolean>();
  private readonly hide$ = new Subject<boolean>();
  loading$: Observable<boolean> = defer(() =>
    merge(
      this.show$.pipe(map(() => 1)),
      this.hide$.pipe(map(() => -1))
    ).pipe(
      scan((acc, curr) => Math.max(0, acc + curr), 0),
      map(count => count > 0)
    )
  );


  constructor(private overlay: Overlay) {
    this.loading$.subscribe(isLoading => {
      if (isLoading) {
        this.showOverlay();
      } else {
        this.hideOverlay();
      }
    });
  }


  show(): void {
    this.show$.next(true);
  }


  hide(): void {
    this.hide$.next(false);
  }


  private showOverlay(): void {
    if (!this.overlayRef) {
      const positionStrategy = this.overlay.position()
        .global()
        .centerHorizontally()
        .centerVertically();

      const overlayConfig = new OverlayConfig({
        hasBackdrop: false,
        positionStrategy
      });

      this.overlayRef = this.overlay.create(overlayConfig);

      const loaderPortal = new ComponentPortal(LoaderComponent);
      this.overlayRef.attach(loaderPortal);
    }
  }

  private hideOverlay(): void {
    if (this.overlayRef) {
      this.overlayRef.dispose();
      this.overlayRef = null;
    }
  }
}
