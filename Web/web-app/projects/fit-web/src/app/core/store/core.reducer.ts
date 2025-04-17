import {CoreState} from '../models/core.model';
import {createReducer, on} from '@ngrx/store';
import {setUser} from './core.actions';

export const coreInitialState: CoreState = {
  user: null
};

export const coreReducer = createReducer(
  coreInitialState,
  on(setUser, (state, {user}) => ({
    ...state,
    user
  }))
)

