import {CanActivateFn, Router} from '@angular/router';
import {inject} from '@angular/core';
import {map, take} from 'rxjs';
import {User} from '../../modules/users/models/user.model';
import {AuthService} from '../services/auth.service';

export const authGuard: CanActivateFn = (route, state) => {
  const authService: AuthService = inject(AuthService);
  const router: Router = inject(Router);

  return authService.getUser().pipe(
    take(1),
    map((auth: User | null) => {
      if (auth !== null) {

        return true;
      }
      return router.createUrlTree(['/']);
    }));
};
