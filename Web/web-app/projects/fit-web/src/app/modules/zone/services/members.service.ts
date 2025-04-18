import {Injectable} from '@angular/core';
import {ApiUsersService, FindUserResponse, ZoneApiService} from 'api';
import {FormGroup} from '@angular/forms';
import {BehaviorSubject, debounceTime, filter, Observable, switchMap} from 'rxjs';
import {FindUser} from '../../../../../../api/src/lib/models/find-user-response.model';
import {Store} from '@ngrx/store';
import {ZonesState} from '../store/zone.reducer';
import {Member} from '../models/member.model';
import {Router} from '@angular/router';


@Injectable()
export class MembersService {
  private _foundMembers: BehaviorSubject<FindUser[]> = new BehaviorSubject<FindUser[]>([]);

  constructor(
    private usersService: ApiUsersService){
  }


  public get foundMembers(): Observable<FindUser[]> {
    return this._foundMembers.asObservable();
  }

  public findUser(form: FormGroup<any>): void {
    form.get("findUser")?.valueChanges.pipe(
      filter(value => !!value &&
        !!value.email &&
        value.email?.length > 3),
      debounceTime(100),
      switchMap(findUserForm => this.usersService.findUserByEmail(findUserForm.email as string)),
      filter(user => !!user.userData && user.userData.length > 0)).subscribe(
      (result: FindUserResponse) => {
        this.addFoundMemberToForm(result)
      }
    );
  }

  public findMembersData(members: Member[]): Observable<FindUserResponse> {
    return this.usersService.findUserByIds(members);
  }


  private addFoundMemberToForm(foundMember: FindUserResponse) {
    this._foundMembers.next(foundMember.userData)
  }
}
