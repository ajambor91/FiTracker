import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {AbstractControl, FormArray, FormControl, FormGroup} from '@angular/forms';
import {ZoneService} from '../../../services/zone.service';
import {FoundUserForm} from '../../../forms/add-members.form';
import {FindUserForm} from '../../../forms/find-user.form';
import {MembersService} from '../../../services/members.service';
import {map, Observable, switchMap, take, tap} from 'rxjs';
import {FindUser} from '../../../models/find-user.model';
import {UpdateZoneForm, updateZoneForm, ZoneDataForm} from '../../../forms/update-zone.form';
import {ActivatedRoute} from '@angular/router';
import {Zone} from '../../../models/zone.model';
import {NavService} from '../../../services/nav.service';
import {faMinus, faPlus} from '@fortawesome/free-solid-svg-icons';
import {SnackbarService} from '../../../../shared/services/snackbar.service';

@Component({
  selector: 'app-update-zone-dialog',
  standalone: false,
  templateUrl: './update-zone-dialog.component.html',
  styleUrl: './update-zone-dialog.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class UpdateZoneDialogComponent implements OnInit {
  public zone$!: Observable<Zone>;
  public faMinus = faMinus;
  public faPlus = faPlus

  private zone!: Zone;

  constructor(
    private membersService: MembersService,
    private zoneService: ZoneService,
    private activatedRoute: ActivatedRoute,
    private navSevice: NavService,
    private snackbar: SnackbarService
  ) {
  }

  private _form: FormGroup<UpdateZoneForm> = updateZoneForm;

  public get form(): FormGroup<UpdateZoneForm> {
    return this._form;
  }

  public get isNameIsInvalid(): boolean {
    const zoneNameControl: AbstractControl = (this.form.get('zoneData') as FormGroup).get('zoneName') as AbstractControl;
    return zoneNameControl.invalid && zoneNameControl.touched;
  }

  public get isDescriptionIsInvalid(): boolean {
    const zoneDescriptionControl: AbstractControl = (this.form.get('zoneData') as FormGroup).get('zoneDescription') as AbstractControl;
    return zoneDescriptionControl.invalid && zoneDescriptionControl.touched;
  }

  public get findUserForm(): FormGroup<FindUserForm> {
    return this._form.get('findUser') as FormGroup<FindUserForm>;
  }

  public get foundUsers(): FormArray<FormGroup> {
    return this._form.get('foundUsers') as FormArray<FormGroup<FoundUserForm>>;
  }

  public get foundUsers$(): Observable<FindUser[]> {
    return this.membersService.foundMembers;
  }

  public get addedMembers(): FormArray<FormGroup> {
    return this._form.get('addedMembers') as FormArray<FormGroup<FoundUserForm>>;
  }

  public get zoneData(): FormGroup<ZoneDataForm> {
    return this._form.get('zoneData') as FormGroup<ZoneDataForm>;
  }

  public ngOnInit(): void {
    this.fillForm();
    this.membersService.findUser(this._form);

  }

  public selectUser(user: FindUser) {
    const {id, name, email} = user;
    this.addedMembers.push(new FormGroup({
      id: new FormControl(id),
      name: new FormControl(name),
      email: new FormControl(email)
    }))
  }

  public submitForm(): void {
    this.zoneService.updateFullZoneData(this.zone.zoneId, this._form);

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

  private fillForm(): void {
    this.zone$ = this.activatedRoute.params.pipe(
      map(param => param['id']),
      switchMap(id => this.zoneService.fillUpdateZoneForm(id, this.form)),
      tap(zone => {
        this.zone = zone;
      }),
      take(1)
    )
  }

}
