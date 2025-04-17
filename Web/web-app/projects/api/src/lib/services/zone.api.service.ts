import {Inject, Injectable} from '@angular/core';
import {BaseService} from './base.service';
import {NewZoneRequest} from '../models/new-zone-request.model';
import {Observable} from 'rxjs';
import {GetZoneResponse} from '../models/get-zone-response.model';
import {NewZoneResponse} from '../models/new-zone-response.model';
import {DeleteZoneResponse} from '../models/delete-zone-response.model';
import {RemoveZoneMemberRequest} from '../models/remove-zone-member-request.model';
import {RemoveZoneMemberResponse} from '../models/remove-zone-member-response.model';
import {UpdateZoneRequest} from '../models/update-zone-request.model';
import {UpdateZoneResponse} from '../models/update-zone-response.model';
import {ZonesResponse} from '../models/zones-response.model';
import {API_BASE_URL} from '../core/tokens';
import {HttpClient} from '@angular/common/http';

@Injectable()
export class ZoneApiService extends BaseService {
  protected readonly PATH = '/zone/zones/';

  constructor(private httpClient: HttpClient, @Inject(API_BASE_URL) private apiBaseUrl: string) {
    super(apiBaseUrl);
  }

  public createZone(zone: NewZoneRequest): Observable<NewZoneResponse> {
    return this.httpClient.post<NewZoneResponse>(`${this.path}zone`, zone);
  }


  public getZone(zoneId: string): Observable<GetZoneResponse> {
    return this.httpClient.get<GetZoneResponse>(`${this.path}zone/${zoneId}`);
  }

  public deleteZone(zoneId: string): Observable<DeleteZoneResponse> {
    return this.httpClient.delete<DeleteZoneResponse>(`${this.path}zone/${zoneId}`);
  }

  public deleteZoneMember(zoneId: string, zone: RemoveZoneMemberRequest): Observable<RemoveZoneMemberResponse> {
    return this.httpClient.patch<RemoveZoneMemberResponse>(`${this.path}zone/${zoneId}/member`, zone);
  }

  public updateZone(zoneId: string, zone: UpdateZoneRequest): Observable<UpdateZoneResponse> {
    return this.httpClient.patch<UpdateZoneResponse>(`${this.path}zone/${zoneId}`, zone);
  }

  public getAllUserZones(): Observable<ZonesResponse> {
    return this.httpClient.get<ZonesResponse>(`${this.path}?all=true`)
  }

  public getLastUserZones(): Observable<ZonesResponse> {
    return this.httpClient.get<ZonesResponse>(this.path);
  }

}
