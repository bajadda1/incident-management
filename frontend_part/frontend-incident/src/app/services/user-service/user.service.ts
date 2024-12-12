import {Injectable} from '@angular/core';
import {environment} from '../../../environments/environment';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';
import {UserResponseDTO} from '../../models/user-response';
import {ApiResponseGenericPagination} from '../../models/api-response';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  url = environment.backendHost
  context = environment.contextPath
  fullURL = this.url + this.context

  constructor(private httpClient: HttpClient) {
  }


  // Get the current user
  getCurrentUser(): Observable<UserResponseDTO> {
    return this.httpClient.get<UserResponseDTO>(`${this.fullURL}/users/me`);
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

  // Get all professionals
  getProfessionals(): Observable<UserResponseDTO[]> {
    return this.httpClient.get<UserResponseDTO[]>(`${this.fullURL}/users`);
  }

  // Get professionals with pagination
  getProfessionalsPagination(
    current: number,
    size: number
  ): Observable<ApiResponseGenericPagination<UserResponseDTO>> {
    const params = new HttpParams()
      .set('current', current.toString())
      .set('size', size.toString());
    return this.httpClient.get<ApiResponseGenericPagination<UserResponseDTO>>(
      `${this.fullURL}/users/pagination`,
      {params}
    );
  }
}
