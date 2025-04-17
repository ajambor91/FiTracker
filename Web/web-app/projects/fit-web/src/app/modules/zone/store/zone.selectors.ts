import {createFeatureSelector, createSelector} from '@ngrx/store';
import {ZonesState} from './zone.reducer';

export const selectZonesState = createFeatureSelector<ZonesState>('zones');

export const selectZones = createSelector(
  selectZonesState,
  (state: ZonesState) => state.zones
);

export const selectCurrentZone = createSelector(
  selectZonesState,
  (state: ZonesState) => state.currentZone
);

export const selectZoneById = (zoneId: string) => createSelector(
  selectZonesState,
  (state: ZonesState) => state.zones.find(zone => zone.zoneId === zoneId)
);
