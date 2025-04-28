import {HttpEvent, HttpHandlerFn, HttpRequest, HttpResponse} from '@angular/common/http';
import {Observable, tap} from 'rxjs';
import {environment} from '../../../environments/environment';

const AUTH_PATH: string = environment.apiUrl + '/main/users/login';

export function jwtResponseInterceptor(req: HttpRequest<unknown>, next: HttpHandlerFn): Observable<HttpEvent<unknown>> {
  return next(req).pipe(
    tap((res) => {

      if (res instanceof HttpResponse && res.url === AUTH_PATH) {
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
