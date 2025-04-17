import {createReducer, on} from '@ngrx/store';
import {addZone, setCurrentZone, setZones, updateZone} from './zone.actions';
import {Zone} from '../models/zone.model';

export interface ZonesState {
  zones: Zone[];
  currentZone: Zone | null;
}

export const zonesInitialState: ZonesState = {
  zones: [],
  currentZone: null
};

export const zoneReducer = createReducer(
  zonesInitialState,
  on(setZones, (state, {zones}) => ({
    ...state,
    zones
  })),
  on(setCurrentZone, (state, {zone}) => ({
    ...state,
    currentZone: zone
  })),
  on(updateZone, (state, {zone}) => ({
    ...state,
    zones: state.zones.map(zoneFromState => {
      if (zoneFromState.zoneId !== zone.zoneId) {
        return zoneFromState;
      }
      return zone;
    })
  })),
  on(addZone, (state, {zone}) => ({
    ...state,
    zones: [zone, ...state.zones].slice(0, -1)
  }))
)

