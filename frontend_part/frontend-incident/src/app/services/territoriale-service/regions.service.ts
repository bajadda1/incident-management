import {Injectable} from '@angular/core';
import {environment} from '../../../environments/environment';
import {Observable} from 'rxjs';
import {HttpClient} from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class RegionsService {

  url = environment.backendHost
  context = environment.contextPath
  fullURL = this.url + this.context


  constructor(private httpClient: HttpClient) {
  }

  getRegions(): Observable<any> {
    return this.httpClient.get<any>(`${this.fullURL}/regions`);
  }

}
