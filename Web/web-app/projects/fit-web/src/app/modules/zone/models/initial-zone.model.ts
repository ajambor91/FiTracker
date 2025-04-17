import {InitialExpenses} from './initial-expenses.model';
import {Zone} from './zone.model';

export interface InitialZone extends InitialExpenses {
  zoneData: Zone;
}
