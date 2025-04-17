import {BaseZoneResponse} from './base-zone-response.model';

export interface Zone extends BaseZoneResponse {

}

export interface ZonesResponse {
  zones: Zone[];
}
