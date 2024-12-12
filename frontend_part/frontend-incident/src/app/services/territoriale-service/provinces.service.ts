import { Injectable } from '@angular/core';
import {environment} from '../../../environments/environment';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ProvincesService {

  url = environment.backendHost
  context = environment.contextPath
  fullURL = this.url + this.context


  constructor(private httpClient: HttpClient) {
  }

  getProvinces(): Observable<any> {
    return this.httpClient.get<any>(`${this.fullURL}/provinces`);
  }
}
