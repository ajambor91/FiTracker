import {createFeatureSelector, createSelector} from '@ngrx/store';
import {CoreState} from '../models/core.model';

export const selectCoreState = createFeatureSelector<CoreState>('core');

export const selectUser = createSelector(
  selectCoreState,
  (state: CoreState) => state.user
);
