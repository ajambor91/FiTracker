import {HttpEvent, HttpHandlerFn, HttpRequest} from '@angular/common/http';
import {Observable} from 'rxjs';

export function jwtRequestInterceptor(req: HttpRequest<unknown>, next: HttpHandlerFn): Observable<HttpEvent<unknown>> {
  const token: string | null = localStorage.getItem("token");
  if (token) {
    req = req.clone({
      headers: req.headers.set("Authorization", `Bearer ${token}`)
    });
  }
  return next(req);
}
