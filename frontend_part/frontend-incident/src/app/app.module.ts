import {CUSTOM_ELEMENTS_SCHEMA, NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {provideAnimationsAsync} from '@angular/platform-browser/animations/async';
import {NavBarComponent} from './components/nav-bar/nav-bar.component';
import {LoginComponent} from './components/login/login.component';
import {SignupComponent} from './components/signup/signup.component';
import {DashboardComponent} from './components/dashboard/dashboard.component';
import {IncidentListComponent} from './components/incident-list/incident-list.component';
import {IncidentItemComponent} from './components/incident-item/incident-item.component';
import {UserManagementComponent} from './components/user-management/user-management.component';
import {HomeComponent} from './components/home/home.component';
import {AboutComponent} from './components/about/about.component';
import {SideBarComponent} from './components/side-bar/side-bar.component';
import {NotFoundComponent} from './components/not-found/not-found.component';
import {FooterComponent} from './components/footer/footer.component';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {FontAwesomeModule} from '@fortawesome/angular-fontawesome';
import {library} from '@fortawesome/fontawesome-svg-core';
import {faCoffee, faHome} from '@fortawesome/free-solid-svg-icons';
import {ServiceSidebarComponent} from './components/service-sidebar/service-sidebar.component';
import {UserItemComponent} from './components/user-item/user-item.component';
import {ProfileComponent} from './components/profile/profile.component';
import {SettingsComponent} from './components/settings/settings.component';
import {
  HTTP_INTERCEPTORS,
  HttpClientModule,
  provideHttpClient,
  withFetch,
  withInterceptorsFromDi
} from '@angular/common/http';
import {AuthInterceptor} from './services/jwt-interceptor/interceptor.service';
import {AuthenticationGuard} from './services/auth-guard/authentication-guard.service';
import {AuthorizationGuard} from './services/auth-guard/authorization-guard.service';
import {MapComponent} from './components/map/map.component';
import {SpinnerComponent} from './components/spinner/spinner.component';
import {UnauthorizedComponent} from './components/unauthorized/unauthorized.component';
import {NgxSpinnerModule} from 'ngx-spinner';
import {SpinnerInterceptor} from './services/spinner-interceptor/spinner.interceptor';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {NgOptimizedImage} from "@angular/common";

library.add(faCoffee, faHome);

@NgModule({
  declarations: [
    AppComponent,
    NavBarComponent,
    LoginComponent,
    SignupComponent,
    DashboardComponent,
    IncidentListComponent,
    IncidentItemComponent,
    UserManagementComponent,
    HomeComponent,
    AboutComponent,
    SideBarComponent,
    NotFoundComponent,
    FooterComponent,

    ServiceSidebarComponent,
    UserItemComponent,
    ProfileComponent,
    SettingsComponent,
    MapComponent,
    SpinnerComponent,
    UnauthorizedComponent
  ],
    imports: [
        BrowserModule,
        AppRoutingModule,
        FormsModule,
        FontAwesomeModule,
        ReactiveFormsModule,
        NgxSpinnerModule,
        BrowserAnimationsModule,
        NgOptimizedImage,
    ],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
  providers: [
    provideAnimationsAsync(),
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: SpinnerInterceptor,
      multi: true,
    },
    provideHttpClient(withInterceptorsFromDi()),
    AuthenticationGuard,
    AuthorizationGuard
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
