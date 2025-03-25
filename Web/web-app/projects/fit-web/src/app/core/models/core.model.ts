import {User} from '../../modules/users/models/user.model';

export interface CoreState {
  user: User | null
}
