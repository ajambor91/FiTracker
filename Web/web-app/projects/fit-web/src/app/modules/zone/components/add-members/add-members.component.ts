import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {FormArray, FormControl, FormGroup} from '@angular/forms';
import {ZoneService} from '../../services/zone.service';
import {addMembersForm, AddMembersForm, FoundUserForm} from '../../forms/add-members.form';
import {FindUserForm} from '../../forms/find-user.form';
import {MembersService} from '../../services/members.service';
import {map, Observable, switchMap} from 'rxjs';
import {FindUser} from '../../models/find-user.model';
import {ActivatedRoute, Router} from '@angular/router';
import {faMinus, faPlus} from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'app-add-members',
  standalone: false,
  templateUrl: './add-members.component.html',
  styleUrl: './add-members.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class AddMembersComponent implements OnInit {
  public faPlus = faPlus;
  public faMinus = faMinus;

  constructor(private membersService: MembersService, private zoneService: ZoneService, private router: Router, private activatedRoute: ActivatedRoute) {
  }

  private _form: FormGroup<AddMembersForm> = addMembersForm;

  public get form(): FormGroup<AddMembersForm> {
    return this._form;
  }

  public get findUserForm(): FormGroup<FindUserForm> {
    return this._form.get('findUser') as FormGroup<FindUserForm>;
  }

  public get foundUsers$(): Observable<FindUser[]> {
    return this.membersService.foundMembers;
  }

  public get addedMembers(): FormArray<FormGroup> {
    return this._form.get('addedMembers') as FormArray<FormGroup<FoundUserForm>>;
  }

  public get foundUsers(): FormArray<FormGroup> {
    return this._form.get('foundUsers') as FormArray<FormGroup<FoundUserForm>>;
  }

  public ngOnInit(): void {
    this.membersService.findUser(this._form);
  }

  public skip(): void {
    this.activatedRoute.params.pipe(
      map(param => param["id"]),
    ).subscribe(id => {
      this.router.navigate(['dashboard', 'zones', id, 'categories', 'add']);
    });
  }


  public selectUser(user: FindUser): void {
    const {id, name, email} = user;
    this.addedMembers.push(new FormGroup({
      id: new FormControl(id),
      name: new FormControl(name),
      email: new FormControl(email)
    }))
  }

  public removeMember(user: FormGroup<FoundUserForm>, index: number): void {
    const {id, name, email} = user.getRawValue();

    this.addedMembers.removeAt(index)
    this.foundUsers.push(new FormGroup({
      id: new FormControl(id),
      name: new FormControl(name),
      email: new FormControl(email)
    }))
  }

  public submitForm(): void {
    this.activatedRoute.params.pipe(
      map(param => param["id"]),
      switchMap(id => this.zoneService.updateZone(id, this.addedMembers.getRawValue() as FindUser[]))
    ).subscribe(updatetZone => {
      this.router.navigate(['dashboard', 'zones', updatetZone.zoneId, 'categories', 'add']);
    });
  }

}
