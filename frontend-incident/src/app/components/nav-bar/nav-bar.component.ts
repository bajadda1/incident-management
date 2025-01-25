import {Component, HostListener, OnInit} from '@angular/core';
import {UserService} from '../../services/auth-service/user.service';
import {data} from 'autoprefixer';
import {Router} from '@angular/router';


@Component({
  selector: 'app-nav-bar',
  templateUrl: './nav-bar.component.html',
  styleUrl: './nav-bar.component.css',
  standalone: false
})
export class NavBarComponent implements OnInit {

  constructor(private userService: UserService,
              private router: Router) {
  }

  isUserAuthenticated!: boolean;
  isAdmin!: boolean;
  username: any;
  isClicked: boolean = false

  ngOnInit(): void {
    this.userService.isAuthenticated$.subscribe(
      (data) => {
        this.isUserAuthenticated = data
        if (data) {
          this.username = this.userService.username;
          this.isAdmin = this.userService.isAdmin;
        }
      },
      (error) => console.log(error)
    )
  }

  logout() {
    this.userService.logout();
  }

}
