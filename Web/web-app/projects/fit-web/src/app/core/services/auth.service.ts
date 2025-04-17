import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {User} from '../../modules/users/models/user.model';
import {Store} from '@ngrx/store';
import {setUser} from '../store/core.actions';
import {selectUser} from '../store/core.selectors';

@Injectable()
export class AuthService {


  constructor(private _state: Store) {
  }

  public loginUser(user: User): void {
    this._state.dispatch(setUser(user))
  }

  public getUser(): Observable<User | null> {
    return this._state.select(selectUser);
  }

  public logoutUser(): void {
    localStorage.removeItem('token');
    this._state.dispatch(setUser(null));
  }
}
