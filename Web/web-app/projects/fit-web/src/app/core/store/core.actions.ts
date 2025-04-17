import {createAction, props} from '@ngrx/store';
import {User} from '../../modules/users/models/user.model';
export const setUser = createAction('[Core] setUser', (user: User | null) => ({user})
);
