import {BaseZone} from './base-zone.model';
import {ZoneMember} from './zone-member.model';

export interface RemoveZoneMemberRequest extends BaseZone {
  membersList: ZoneMember[];
}
