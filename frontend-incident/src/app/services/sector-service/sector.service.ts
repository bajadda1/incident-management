import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {SectorDTO} from '../../models/sector';
import {environment} from '../../../environments/environment';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class SectorService {

  url = environment.backendHost
  context = environment.contextPath
  fullURL = this.url + this.context

  constructor(private httpClient: HttpClient) {
  }

  getSectors(): Observable<SectorDTO[]> {
    return this.httpClient.get<SectorDTO[]>(`${this.fullURL}/sectors`)
  }

  getSector(id: number): Observable<SectorDTO> {
    return this.httpClient.get<SectorDTO>(`${this.fullURL}/sectors/${id}`)
  }

  addSector(sector: SectorDTO): Observable<SectorDTO> {
    return this.httpClient.post<SectorDTO>(`${this.fullURL}/sectors`, sector)
  }


  updateSector(sector: SectorDTO): Observable<SectorDTO> {
    return this.httpClient.put<SectorDTO>(`${this.fullURL}/sectors`, sector)
  }

  deleteSector(id: number): Observable<string> {
    return this.httpClient.delete<string>(`${this.fullURL}/sectors/${id}`)
  }
}
