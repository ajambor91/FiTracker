import {BaseZone} from './base-zone.model';
import {ZoneMember} from './zone-member.model';

export interface DeleteZoneResponse extends BaseZone {
  membersList: ZoneMember[];
}
