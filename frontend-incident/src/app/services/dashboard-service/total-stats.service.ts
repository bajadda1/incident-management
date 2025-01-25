import {Injectable} from '@angular/core';
import {environment} from '../../../environments/environment';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';
import {SectorDTO} from '../../models/sector';
import {TotalStats} from '../../models/total-stats';
import {IncidentStatusGroupDTO} from '../../models/Incident-status-group';
import {ApiResponseGenericPagination} from '../../models/api-response';
import {IncidentDTO} from '../../models/incident';
import {formatDate} from '@angular/common';

@Injectable({
  providedIn: 'root'
})
export class TotalStatsService {

  url = environment.backendHost
  context = environment.contextPath
  fullURL = this.url + this.context

  constructor(private httpClient: HttpClient) {
  }

  getTotalStats(): Observable<TotalStats> {
    return this.httpClient.get<TotalStats>(`${this.fullURL}/stats`)
  }

  getIncidentGroupedByStatus(): Observable<IncidentStatusGroupDTO[]> {
    return this.httpClient.get<IncidentStatusGroupDTO[]>(`${this.fullURL}/stats/grouped-by-status`)
  }

  gettGroupedByStatus(filters: {
    startDate: Date | null;
    endDate: Date | null;
    sectorId: number | null;
    regionId: any;
    typeId: any;
  }): Observable<IncidentStatusGroupDTO[]> {
    let params = new HttpParams();

    // Append filter parameters if they are defined

    if (filters.regionId) params = params.set('region', filters.regionId.toString());
    if (filters.sectorId !== null && filters.sectorId !== undefined) params = params.set('sector', filters.sectorId.toString());
    if (filters.typeId) params = params.set('type', filters.typeId.toString());
    if (filters.startDate) params = params.set('start-date', formatDate(filters.startDate, 'yyyy-MM-dd', 'en-US'));
    if (filters.endDate) params = params.set('end-date', formatDate(filters.endDate, 'yyyy-MM-dd', 'en-US'));
    console.log('Final Request Params:', params.toString());

    return this.httpClient.get<IncidentStatusGroupDTO[]>(`${this.fullURL}/stats/search/grouped-by-status`, {params});
  }


}
