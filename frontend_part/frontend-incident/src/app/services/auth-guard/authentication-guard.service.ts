import {
  ActivatedRouteSnapshot,
  CanActivate,
  CanActivateFn,
  GuardResult,
  MaybeAsync, Router,
  RouterStateSnapshot
} from '@angular/router';
import {Injectable} from '@angular/core';
import {UserService} from '../auth-service/user.service';
import {data} from 'autoprefixer';
import {map} from 'rxjs';

@Injectable()
export class AuthenticationGuard {
  isCurrentlyLoggedIn!: boolean;

  constructor(private userService: UserService, private router: Router) {
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): MaybeAsync<GuardResult> {

    return this.userService.isAuthenticated$.pipe(
      map((isAuthenticated) => {
        if (!isAuthenticated) {
          this.router.navigateByUrl('/login'); // Redirect to login if not authenticated
          return false;
        }
        return true;
      })
    );  }

}
