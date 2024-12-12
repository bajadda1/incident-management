import {
  ActivatedRouteSnapshot,
  CanActivate,
  CanActivateFn,
  GuardResult,
  MaybeAsync, Router,
  RouterStateSnapshot
}
  from '@angular/router';
import {Injectable} from '@angular/core';
import {UserService} from '../auth-service/user.service';
import {map} from 'rxjs';

@Injectable()
export class AuthorizationGuard {
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

        const userRoles = this.userService.authorities || [];
        const requiredRole = route.data['roles'];
        if (!userRoles.includes(requiredRole)) {
          this.router.navigateByUrl('/unauthorized'); // Redirect if the user does not have the required role
          return false;
        }

        return true; // Allow access if authenticated and has the required role
      })
    );
  }

}
