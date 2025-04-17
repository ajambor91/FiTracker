import {BaseZone} from './base-zone.model';
import {ZoneMember} from './zone-member.model';

export interface UpdateZoneRequest extends BaseZone {
  membersList: ZoneMember[];
}
