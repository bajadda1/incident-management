import {Injectable} from '@angular/core';
import {environment} from '../../../environments/environment';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {RegionDTO} from '../../models/region';
import {TypeDTO} from '../../models/type';

@Injectable({
  providedIn: 'root'
})
export class TypeService {

  url = environment.backendHost
  context = environment.contextPath
  fullURL = this.url + this.context


  constructor(private httpClient: HttpClient) {
  }

  getTypes(): Observable<TypeDTO[]> {
    return this.httpClient.get<TypeDTO[]>(`${this.fullURL}/types`);
  }

  addType(sectorId: number, typeDTO: TypeDTO): Observable<TypeDTO[]> {
    return this.httpClient.post<TypeDTO[]>(`${this.fullURL}/types/${sectorId}`, typeDTO);
  }
}
