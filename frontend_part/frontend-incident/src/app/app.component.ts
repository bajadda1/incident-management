import {AfterViewChecked, Component, OnInit} from '@angular/core';

// Initialization for ES Users
import {
  Dropdown,
  Ripple,
  Collapse,
  initTWE,
  Modal
} from "tw-elements";

// Initialization for ES Users


initTWE({Modal, Ripple});
import {
  ActivatedRoute,
  NavigationCancel,
  NavigationEnd,
  NavigationError,
  NavigationStart,
  Router
} from '@angular/router';
import {UserService} from './services/auth-service/user.service';

import {NgxSpinnerService} from 'ngx-spinner';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit, AfterViewChecked {
  title = 'frontend-incident';
  hideNavAndFooter: boolean = false;

  constructor(private router: Router,
              private userService: UserService,
              protected spinner: NgxSpinnerService) {
  }

  ngOnInit(): void {
    initTWE({Dropdown, Ripple, Collapse, Modal});

    const token = localStorage.getItem('JWT_TOKEN');
    if (token) {
      this.userService.loadProfile(token);
      this.userService.setAuthenticated(this.userService.exp > Date.now());
    }


    // Show spinner
    this.spinner.show();

    // Hide spinner after some time
    setTimeout(() => {
      this.spinner.hide();
    }, 3000);


    // Listen to router events
    this.router.events.subscribe(event => {
      if (event instanceof NavigationStart) {
        // Show the spinner on route change start
        this.spinner.show();
      }

      if (
        event instanceof NavigationEnd ||
        event instanceof NavigationCancel ||
        event instanceof NavigationError
      ) {
        // Hide the spinner on route change end or error
        setTimeout(() => {
          this.spinner.hide();
        }, 300); // Optional delay to prevent quick flashing
      }
    });

  }

  ngAfterViewChecked(): void {
    initTWE({Dropdown, Ripple, Collapse,Modal});// Reinitialize whenever view changes
  }

}
