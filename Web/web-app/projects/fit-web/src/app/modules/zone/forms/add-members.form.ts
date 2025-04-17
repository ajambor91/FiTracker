import {FormArray, FormControl, FormGroup} from '@angular/forms';
import {findUserForm, FindUserForm} from './find-user.form';


export interface AddMembersForm {
  findUser: FormGroup<FindUserForm>;
  foundUsers: FormArray<FormGroup<FoundUserForm>>;
  addedMembers: FormArray<FormGroup<FoundUserForm>>;
}

export interface FoundUserForm {
  id: FormControl<number | null>;
  name: FormControl<string | null>;
  email: FormControl<string | null>;
}

export const addMembersForm: FormGroup<AddMembersForm> = new FormGroup<AddMembersForm>({
  findUser: findUserForm,
  foundUsers: new FormArray<FormGroup<{
    id: FormControl<number | null>;
    name: FormControl<string | null>;
    email: FormControl<string | null>;
  }>>([]),
  addedMembers: new FormArray<FormGroup<{
    id: FormControl<number | null>;
    name: FormControl<string | null>;
    email: FormControl<string | null>;
  }>>([])
})
