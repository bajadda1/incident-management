import {Injectable} from '@angular/core';
import {environment} from '../../../environments/environment';
import {HttpClient} from '@angular/common/http';
import {UserRegisterDTO} from '../../models/user-register';
import {JwtDTO} from '../../models/jwt';
import {BehaviorSubject, catchError, map, Observable, of, throwError} from 'rxjs';
import {UserLoginDTO} from '../../models/user-login';
import {jwtDecode} from 'jwt-decode';
import {stringify} from 'postcss';
import {Router} from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  url = environment.backendHost
  context = environment.contextPath
  fullURL = this.url + this.context

  jwt!: any;
  isLoggedIn: boolean = false;
  sub!: any;
  username: any
  authorities!: any;
  isAdmin!: boolean;
  exp!: any;
  private isAuthenticatedSubject = new BehaviorSubject<boolean>(false);
  isAuthenticated$ = this.isAuthenticatedSubject.asObservable();

  // Method to set authentication state
  setAuthenticated(isAuthenticated: boolean): void {
    this.isAuthenticatedSubject.next(isAuthenticated);
  }

  // Optional: Method to get the current state
  getAuthenticated(): boolean {
    return this.isAuthenticatedSubject.getValue();
  }

  constructor(private httpClient: HttpClient, private router: Router) {
  }

  signup(userRegisterDTO: UserRegisterDTO) {
    return this.httpClient.post(`${this.fullURL}/auths/register`, userRegisterDTO)
  }

  login(userLoginDTO: UserLoginDTO): Observable<any> {
    return this.httpClient.post<any>(`${this.fullURL}/auths/login`, userLoginDTO)
      .pipe(
        map(response => {
          localStorage.setItem('JWT_TOKEN', response['jwt']);
          this.jwt = response['jwt']

          this.loadProfile(this.jwt)

          return response; // Pass the response forward

        }),
        catchError(error => {
          console.log(error);
          this.isLoggedIn = false;
          return throwError(() => error); // Rethrow the error
        })
      );
  }

  logout(): void {
    localStorage.removeItem('JWT_TOKEN');
    this.isLoggedIn = false;
    this.setAuthenticated(false);
    this.router.navigateByUrl("/login")
  }

  loadProfile(jwt: any) {
    let decodedJWT: any = jwtDecode(jwt);
    console.log(decodedJWT)
    //extract email
    this.sub = decodedJWT['sub']
    console.log(this.sub)
    //extract roles
    this.authorities = decodedJWT['authorities']
    console.log(this.authorities)
    //extract username
    this.username = decodedJWT['fullname']
    console.log(this.username)
    this.exp = decodedJWT['exp'] * 1000
    console.log(this.exp)
    console.log(Date.now())
    this.isAdmin = this.authorities[0] == "admin"
    console.log("IS ADMIN ???")
    console.log(this.isAdmin)
    this.isLoggedIn = true;
    this.setAuthenticated(true);
  }

  isAuthenticated(): boolean {
    return this.isLoggedIn;
  }

}
