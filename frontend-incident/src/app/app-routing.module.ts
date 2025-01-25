import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {HomeComponent} from './components/home/home.component';
import {IncidentListComponent} from './components/incident-list/incident-list.component';
import {IncidentItemComponent} from './components/incident-item/incident-item.component';
import {LoginComponent} from './components/login/login.component';
import {SignupComponent} from './components/signup/signup.component';
import {DashboardComponent} from './components/dashboard/dashboard.component';
import {UserManagementComponent} from './components/user-management/user-management.component';
import {AboutComponent} from './components/about/about.component';
import {ServiceSidebarComponent} from './components/service-sidebar/service-sidebar.component';
import {AuthenticationGuard} from './services/auth-guard/authentication-guard.service';
import {AuthorizationGuard} from './services/auth-guard/authorization-guard.service';
import {MapComponent} from './components/map/map.component';
import {ProfileComponent} from './components/profile/profile.component';
import {UnauthorizedComponent} from './components/unauthorized/unauthorized.component';
import {NotFoundComponent} from './components/not-found/not-found.component';
import {SettingsComponent} from './components/settings/settings.component';

const routes: Routes = [
  {
    path: "",
    component: HomeComponent,
  },

  {
    path: "login",
    component: LoginComponent,
  },
  {
    path: "signup",
    component: SignupComponent,
  },
  {
    path: "about",
    component: AboutComponent,
  },
  {
    path: "dash-board",
    component: DashboardComponent,
  },
  {
    path: "map",
    component: MapComponent,
  },
  {
    path: "unauthorized",
    component: UnauthorizedComponent,
  },
  {
    path: "profile",
    component: ProfileComponent,
    canActivate: [AuthenticationGuard]
  },
  {
    path: "settings",
    component: SettingsComponent,
    canActivate: [AuthorizationGuard],
    data: {roles: "admin"}
  },
  //services for admin and professional
  {
    path: "services",
    component: ServiceSidebarComponent,
    canActivate: [AuthenticationGuard],
    children: [

      {
        path: "incidents",
        component: IncidentListComponent,
      },
      {
        path: "incidents/:id",
        component: IncidentItemComponent,
      },
      {
        path: "users",
        component: UserManagementComponent,
        canActivate: [AuthorizationGuard],
        data: {roles: "admin"}
      },
      {
        path: "users/:id",
        component: UserManagementComponent,
        canActivate: [AuthorizationGuard],
        data: {roles: "admin"}
      },

    ]
  },
  {
    component: NotFoundComponent,
    path: '**'
  }

];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
