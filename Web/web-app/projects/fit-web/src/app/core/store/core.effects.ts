// import {Actions, createEffect, ofType} from '@ngrx/effects';
// import {inject} from '@angular/core';
// import {CoreService} from '../services/core.service';
// import {getToken, setToken} from './core.actions';
// import {map, switchMap} from 'rxjs';
//
//
// export const getUser = createEffect(
//   // 1. Dodaj typowanie parametrów
//   (actions$ = inject(Actions), coreService = inject(CoreService)) => {
//     return actions$.pipe(
//       ofType(getToken),
//       switchMap(() =>
//         coreService.getCsrfToken().pipe(
//           // 2. Poprawne przekazanie wartości do akcji
//           map((response: {token: string}) => setToken(response))
//         )
//       )
//     );
//   },
//   { functional: true } // 3. Wymagana flaga dla efektów funkcyjnych
// );
