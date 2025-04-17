import {BaseZone} from './base-zone.model';
import {ZoneMember} from './zone-member.model';

export interface BaseZoneResponse extends BaseZone {
  membersList: ZoneMember[];
  createdAt: string;
  ownerId: number,
  zoneId: string;
}
