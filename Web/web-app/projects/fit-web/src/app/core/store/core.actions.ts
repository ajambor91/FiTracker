import {createAction, props} from '@ngrx/store';
import {User} from '../../modules/users/models/user.model';

export const getToken = createAction('[Core] getToken');
export const setToken = createAction('[Core] setToken', props<{ token: string | null }>());

export const getUser = createAction('[Core] getUser');
export const setUser = createAction('[Core] setUser', (user: User | null) => ({user})
);
