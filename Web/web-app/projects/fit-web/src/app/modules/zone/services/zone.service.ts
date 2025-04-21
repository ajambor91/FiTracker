import {Injectable} from '@angular/core';
import {
  FindUser,
  NewZoneRequest,
  UpdateZoneRequest,
  UpdateZoneResponse,
  ZoneApiService,
  ZoneMember,
  ZonesResponse
} from 'api';
import {Router} from '@angular/router';
import {catchError, filter, forkJoin, map, Observable, of, shareReplay, switchMap, take, tap} from 'rxjs';
import {select, Store} from '@ngrx/store';
import {ZonesState} from '../store/zone.reducer';
import {addZone, getZones, refreshZone, setCurrentZone, updateZone} from '../store/zone.actions';
import {Zone} from '../models/zone.model';
import {selectCurrentZone, selectZoneById, selectZones} from '../store/zone.selectors';

import {FormArray, FormControl, FormGroup} from '@angular/forms';
import {UpdateZoneForm, ZoneDataForm} from '../forms/update-zone.form';
import {FoundUserForm} from '../forms/add-members.form';
import {Member} from '../models/member.model';
import {MembersService} from './members.service';
import {InitialZone} from '../models/initial-zone.model';
import {ExpensesService} from './expenses.service';


@Injectable()
export class ZoneService {

  constructor(
    private zoneApi: ZoneApiService,
    private router: Router,
    private membersService: MembersService,
    private expensesService: ExpensesService,
    private store: Store<ZonesState>) {
  }

  public addNewZone(zone: NewZoneRequest): void {
    this.zoneApi.createZone(zone).subscribe(res => {
      this.store.dispatch(setCurrentZone(res))
      this.store.dispatch(addZone(res));
      this.router.navigate(['dashboard', 'zones', res.zoneId, 'members', 'add'])
    });
  }

  public fillUpdateZoneForm(id: string, form: FormGroup<UpdateZoneForm>): Observable<Zone> {

    return this.getCurrentZoneById(id).pipe(
      take(1),
      filter(zoneData => !!zoneData),
      switchMap((zone: Zone) => {
        const zoneDataForm: FormGroup<ZoneDataForm> = form.get('zoneData') as FormGroup<ZoneDataForm>;
        zoneDataForm.get('zoneName')?.setValue(zone.zoneName);
        zoneDataForm.get('zoneDescription')?.setValue(zone.zoneDescription ?? null);
        return forkJoin([of(zone), this.membersService.findMembersData(zone?.membersList as Member[])]);
      }),
      tap(([_, members]) => {
        const membersForm: FormArray<FormGroup<FoundUserForm>> = form.get('addedMembers') as FormArray<FormGroup<FoundUserForm>>;
        members.userData.forEach(member => {
          membersForm.push(new FormGroup<FoundUserForm>({
            id: new FormControl(member.id),
            name: new FormControl(member.name),
            email: new FormControl(member.email)
          }))
        })
      }),
      map(([zone, _]) => zone)
    );
  }

  public fetchAllUserZones(): void {
    this.store.dispatch(getZones());
  }

  public getInitialZoneData(zoneId: string, date?: Date): Observable<InitialZone> {
    return this.getCurrentZoneById(zoneId).pipe(
      filter(zone => !!zone),
      switchMap(zone => forkJoin([of(zone), this.expensesService.getInitialData(zoneId, date)])),
      map(([zone, expenses]) => ({
        expenses: expenses.expenses,
        zoneData: zone as Zone,
        byCategory: expenses.byCategory,
        byDate: expenses.byDate,
        sum: expenses.sum
      }))
    )
  }

  public refreshZone(zoneId: string): void {
    this.store.dispatch(refreshZone(zoneId))
  }

  public getCurrentZoneById(zoneId: string): Observable<Zone | null> {
    return this.store.pipe(select(selectZoneById(zoneId))).pipe(
      switchMap(zone => {

        if (zone) {
          return of(zone);
        } else {
          return this.zoneApi.getZone(zoneId).pipe(
            tap((zoneRes: Zone) => this.store.dispatch(addZone(zoneRes))),
            map(zoneRes => zoneRes as Zone),
            catchError(() => of(null))
          );
        }
      })
    );
  }

  public updateFullZoneData(zoneId: string, form: FormGroup<UpdateZoneForm>): Observable<Zone> {
    const membersForm: FormArray<FormGroup<FoundUserForm>> = form.get('addedMembers') as FormArray<FormGroup<FoundUserForm>>;
    const zoneDataForm: FormGroup<ZoneDataForm> = form.get('zoneData') as FormGroup<ZoneDataForm>;
    const zoneMembers: ZoneMember[] = membersForm.getRawValue().map(member => ({
      userId: member.id as number,
      role: "MEMBER",
      name: member.name as string
    }));
    const zoneDescription: string | undefined = zoneDataForm.get("zoneDescription")?.value as string | undefined;
    const zoneName: string = zoneDataForm.get("zoneName")?.value as string;
    const updateZoneRequest: UpdateZoneRequest = {
      membersList: zoneMembers,
      zoneDescription: zoneDescription,
      zoneName: zoneName,
      zoneId: zoneId
    };
    return this.zoneApi.updateZone(zoneId, updateZoneRequest).pipe(
      tap(responseZone => {

        this.store.dispatch(updateZone(responseZone));
        this.store.dispatch(setCurrentZone(responseZone))
      }));

  }

  public getCurrentZone(): Observable<Zone | null> {
    return this.store.select(selectCurrentZone).pipe(shareReplay());
  }

  public getLastUserZones(): Observable<ZonesResponse> {
    return this.zoneApi.getLastUserZones();
  }

  public getZoneFromApiById(zoneId: string): Observable<Zone> {
    return this.zoneApi.getZone(zoneId);
  }

  public getAllUserZonesFromApi(): Observable<ZonesResponse> {
    return this.zoneApi.getAllUserZones();
  }

  public getAllUserZonesOnInit(): Observable<Zone[]> {
    return this.store.select(selectZones).pipe(tap(zones => {
      if (zones && zones.length > 0) {
        this.selectCurrentZoneAndGo(zones[0])
      }

    }));
  }

  public selectCurrentZone(zone: Zone): void {
    this.store.dispatch(setCurrentZone(zone));
  }

  public selectCurrentZoneAndGo(zone: Zone): void {
    this.selectCurrentZone(zone);
    this.router.navigate(['/dashboard/zones/overview', zone.zoneId])
  }

  public updateZone(zoneId: string, formData: FindUser[]): Observable<UpdateZoneResponse> {
    return this.store.select(selectCurrentZone).pipe(
      filter((currentZone): currentZone is Zone => currentZone !== null),
      take(1),
      switchMap((currentZone: Zone) => {
        const newZone: Zone = {
          zoneId: zoneId,
          ownerId: currentZone.ownerId,
          zoneDescription: currentZone.zoneDescription,
          zoneName: currentZone.zoneName,
          createdAt: currentZone.createdAt,
          membersList: currentZone.membersList.map(zone => zone)
        };
        const members: Member[] = formData.map(foundMember => ({
          name: foundMember.name,
          userId: foundMember.id,
          role: "MEMBER"
        }))
        newZone.membersList.push(...members);

        return this.zoneApi.updateZone(zoneId, newZone);
      }),
      tap(updatedZone => {
        this.store.dispatch(updateZone(updatedZone))
        this.store.dispatch(setCurrentZone(updatedZone))
      })
    );
  }
}
