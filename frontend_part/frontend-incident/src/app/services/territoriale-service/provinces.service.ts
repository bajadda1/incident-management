import {Injectable} from '@angular/core';
import {environment} from '../../../environments/environment';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {ProvinceDTO} from '../../models/province';

@Injectable({
  providedIn: 'root'
})
export class ProvincesService {

  url = environment.backendHost
  context = environment.contextPath
  fullURL = this.url + this.context


  constructor(private httpClient: HttpClient) {
  }

  getProvinces(): Observable<ProvinceDTO[]> {
    return this.httpClient.get<ProvinceDTO[]>(`${this.fullURL}/provinces`);
  }
}
