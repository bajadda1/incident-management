import {Injectable} from '@angular/core';
import {environment} from '../../../environments/environment';
import {HttpClient, HttpParams} from '@angular/common/http';
import {catchError, map, Observable, throwError} from 'rxjs';
import {UserResponseDTO} from '../../models/user-response';
import {ApiResponseGenericPagination} from '../../models/api-response';
import {SectorDTO} from '../../models/sector';
import {IncidentDTO} from '../../models/incident';

@Injectable({
  providedIn: 'root'
})
export class UtilisateurService {

  url = environment.backendHost
  context = environment.contextPath
  fullURL = this.url + this.context
  professionalSector!: SectorDTO

  constructor(private httpClient: HttpClient) {
  }


  // Get the current user
  getCurrentUser(): Observable<UserResponseDTO> {
    return this.httpClient.get<UserResponseDTO>(`${this.fullURL}/users/me`).pipe(
      map(response => {
        this.professionalSector = response.sectorDTO;

        return response; // Pass the response forward

      }),
      catchError(error => {
        return throwError(() => error); // Rethrow the error
      })
    );
  }

  // Enable a user by email
  enableUserByEmail(username: string): Observable<UserResponseDTO> {
    return this.httpClient.post<UserResponseDTO>(
      `${this.fullURL}/enable-account`,
      username
    );
  }

  // Disable a user by email
  disableUserByEmail(username: string): Observable<UserResponseDTO> {
    return this.httpClient.post<UserResponseDTO>(
      `${this.fullURL}/users/disable-account`,
      username
    );
  }

  // Get a user by ID
  getUserById(id: number): Observable<UserResponseDTO> {
    return this.httpClient.get<UserResponseDTO>(`${this.fullURL}/users/${id}`);
  }

  // Enable a user by ID
  enableUserById(id: number): Observable<UserResponseDTO> {
    return this.httpClient.post<UserResponseDTO>(
      `${this.fullURL}/users/enable-account/${id}`,
      null
    );
  }

  // Disable a user by ID
  disableUserById(id: number): Observable<UserResponseDTO> {
    return this.httpClient.post<UserResponseDTO>(
      `${this.fullURL}/users/disable-account/${id}`,
      null
    );
  }

  searchProfessionals(filters: {
    enabled: boolean | null;
    sectorId: number | null;
    username: string;
    fullname: string;
  }, page: number, size: number): Observable<ApiResponseGenericPagination<UserResponseDTO>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    // Append filter parameters if they are defined
    if (filters.enabled != null) params = params.set('enabled', filters.enabled.toString());
    if (filters.sectorId !== null && filters.sectorId !== undefined) params = params.set('sector', filters.sectorId.toString());
    if (filters.username) params = params.set('username', filters.username);
    if (filters.fullname) params = params.set('fullname', filters.fullname);

    console.log('Final Request Params:', params.toString());

    return this.httpClient.get<any>(`${this.fullURL}/users`, {params});
  }
}
