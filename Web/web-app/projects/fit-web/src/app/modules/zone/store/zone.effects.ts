
import {Actions, createEffect, ofType} from '@ngrx/effects';
import {inject} from '@angular/core';
import {ZoneService} from '../services/zone.service';
import {addZone, getZones, refreshZone, refreshZones, setZones, updateZone} from './zone.actions';
import {map, switchMap} from 'rxjs';
import {GetZoneResponse as ApiZone, GetZoneResponse, ZonesResponse} from 'api';
import {Zone} from '../models/zone.model';

export const zonesEffects = {
  getZonesEffect: createEffect(
    (actions$ = inject(Actions), zoneService = inject(ZoneService)) => {
      return actions$.pipe(
        ofType(getZones),
        switchMap(() =>
          zoneService.getLastUserZones().pipe(
            map((response: ZonesResponse) => {
              const zones: Zone[] = response.zones.map((apiZone: ApiZone) => ({
                ...apiZone,
              }));
              return setZones(zones);
            })
          )
        )
      );
    },
    {functional: true}
  ),
  getZoneEffect: createEffect(
    (actions$ = inject(Actions), zoneService = inject(ZoneService)) => {
      return actions$.pipe(
        ofType(refreshZone),
        switchMap(({zoneId}) =>
          zoneService.getZoneFromApiById(zoneId).pipe(
            map((response: GetZoneResponse) => {
              const zone: Zone = response;
              return updateZone(zone);
            })
          )
        )
      );
    },
    {functional: true}
  ),
  refreshZones: createEffect(
    (actions$ = inject(Actions), zoneService = inject(ZoneService)) => {
      return actions$.pipe(
        ofType(refreshZones),
        switchMap(({zoneId}) =>
          zoneService.getZoneFromApiById(zoneId).pipe(
            map((response: GetZoneResponse) => {
              const zone: Zone = response;
              return addZone(zone);
            })
          )
        )
      );
    },
    {functional: true}
  ),
};
