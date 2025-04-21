import {createAction} from '@ngrx/store';
import {Zone} from '../models/zone.model';

export const setZones = createAction('[Zone] setZones', (zones: Zone[]) => ({zones}));
export const getZones = createAction('[Zone] getZones');

export const setCurrentZone = createAction('[Zone] setCurrentZone', (zone: Zone) => ({zone}));
export const addZone = createAction('[Zone] addZone', (zone: Zone) => ({zone}));

export const updateZone = createAction('[Zone] updateZone', (zone: Zone) => ({zone}));

export const removeZone = createAction('[Zone] removeZone', (zone: Zone) => ({zone}));

export const refreshZone = createAction('[Zone] refreshZone', (zoneId: string) => ({zoneId}))
export const refreshZones = createAction('[Zone] refreshZones', (zoneId: string) => ({zoneId}))

export const getZone = createAction('[Zone] getZone', (zoneId: string) => ({zoneId}))
