import {HttpEvent, HttpHandlerFn, HttpRequest} from '@angular/common/http';
import {Observable} from 'rxjs';
import {csrfUtil} from '../utils/csrf.util';

const CSRF_COOKIE_NAME: string = 'XSRF-TOKEN';
const CSRF_HEADER_VALUE: string = 'X-XSRF-TOKEN';

export function csrfInterceptor(req: HttpRequest<unknown>, next: HttpHandlerFn): Observable<HttpEvent<unknown>> {
  const csrfValue: string | null = csrfUtil.extractCsrfToken(document.cookie, CSRF_COOKIE_NAME);
  if (csrfValue) {
    req = req.clone({
      headers: req.headers.set(CSRF_HEADER_VALUE, csrfValue)
    });
  }
  req = req.clone({
    withCredentials: true
  })

  return next(req);
}
