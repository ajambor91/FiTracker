import {Member} from './member.model';

export interface Zone {
  ownerId: number;
  createdAt: string;
  zoneId: string;
  zoneName: string;
  membersList: Member[];
  zoneDescription?: string | null | undefined;
}

