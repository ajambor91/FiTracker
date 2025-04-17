import {FormControl, FormGroup} from '@angular/forms';


export interface FindUserForm {
  email: FormControl<string | null>;
}

export const findUserForm: FormGroup<FindUserForm> = new FormGroup<FindUserForm>({
  email: new FormControl('', {nonNullable: false})
})
