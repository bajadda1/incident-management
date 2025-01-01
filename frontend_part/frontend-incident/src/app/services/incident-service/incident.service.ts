import {Injectable} from '@angular/core';
import {environment} from '../../../environments/environment';
import {HttpClient, HttpParams} from '@angular/common/http';
import {IncidentDTO} from '../../models/incident';
import {Observable} from 'rxjs';
import {ApiResponseGenericPagination} from '../../models/api-response';
import {StatusDTO} from '../../models/status-dto';
import {Status} from '../../enums/status';
import {RejectionDTO} from '../../models/rejection';

@Injectable({
  providedIn: 'root'
})
export class IncidentService {

  url = environment.backendHost
  context = environment.contextPath
  fullURL = this.url + this.context

  constructor(private httpClient: HttpClient) {
  }


  updateIncidentStatus(incidentId: number, status: string): Observable<IncidentDTO> {
    const statusDTO: StatusDTO = {status: status as Status};
    return this.httpClient.put<any>(
      `${this.fullURL}/incidents/update/${incidentId}`,
      statusDTO
    );


  }

  getIncidentsById(id: number) {
    return this.httpClient.get<IncidentDTO>(`${this.fullURL}/incidents/${id}`);
  }

  rejectIncident(incidentId: number, rejectionDTO: RejectionDTO): Observable<IncidentDTO> {
    return this.httpClient.post<IncidentDTO>(`${this.fullURL}/incidents/reject/${incidentId}`, rejectionDTO);
  }


  searchIncidents(filters: {
    date: string;
    sectorId: number | null;
    regionId: any;
    description: string;
    typeId: any;
    provinceId: any;
    status: string
  }, page: number, size: number): Observable<ApiResponseGenericPagination<IncidentDTO>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    // Append filter parameters if they are defined
    if (filters.status) params = params.set('status', filters.status);
    if (filters.provinceId) params = params.set('province', filters.provinceId.toString());
    if (filters.regionId) params = params.set('region', filters.regionId.toString());
    if (filters.sectorId !== null && filters.sectorId !== undefined) params = params.set('sector', filters.sectorId.toString());
    if (filters.typeId) params = params.set('type', filters.typeId.toString());
    if (filters.description) params = params.set('description', filters.description);
    if (filters.date) params = params.set('date', filters.date);

    console.log('Final Request Params:', params.toString());

    return this.httpClient.get<any>(`${this.fullURL}/incidents/search/admin`, {params});
  }

  searchIncidentsByProfessional(filters: {
    status?: string | null;
    provinceId?: number | null;
    regionId?: number | null;
    sectorId: number | null;
    typeId?: number | null;
    description?: string | null;
    date?: string;
  }, page: number, size: number): Observable<ApiResponseGenericPagination<IncidentDTO>> {
    // Check if sectorId is null
    if (!filters.sectorId) {
      throw new Error('Sector ID is required and cannot be null.');
    }
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sector', filters.sectorId.toString());

    // Append filter parameters if they are defined
    if (filters.status) params = params.set('status', filters.status);
    if (filters.provinceId) params = params.set('province', filters.provinceId.toString());
    if (filters.regionId) params = params.set('region', filters.regionId.toString());
    if (filters.typeId) params = params.set('type', filters.typeId.toString());
    if (filters.description) params = params.set('description', filters.description);
    if (filters.date) params = params.set('date', filters.date);

    return this.httpClient.get<any>(`${this.fullURL}/incidents/search/professional`, {params});
  }

}
