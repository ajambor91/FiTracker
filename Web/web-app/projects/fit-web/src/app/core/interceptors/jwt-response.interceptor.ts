import {HttpEvent, HttpHandlerFn, HttpRequest, HttpResponse} from '@angular/common/http';
import {Observable, tap} from 'rxjs';
import {urlPath} from '../utils/url-path.util';

const AUTH_PATH: string = 'api/main/users/login';

export function jwtResponseInterceptor(req: HttpRequest<unknown>, next: HttpHandlerFn): Observable<HttpEvent<unknown>> {
  return next(req).pipe(
    tap((res) => {
      if (res instanceof HttpResponse && urlPath.extract(res.url as string) === AUTH_PATH) {
        const response = res as HttpResponse<unknown>;
        if (response.headers.has("Authorization")) {
          const token: string | null = response.headers.get("Authorization");
          if (token) {
            localStorage.setItem("token", token);
          }
        }
      }

    })
  );

}
