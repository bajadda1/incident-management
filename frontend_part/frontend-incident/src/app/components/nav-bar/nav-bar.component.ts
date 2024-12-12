import {Component, HostListener, OnInit} from '@angular/core';
import {UserService} from '../../services/auth-service/user.service';
import {data} from 'autoprefixer';


@Component({
  selector: 'app-nav-bar',
  templateUrl: './nav-bar.component.html',
  styleUrl: './nav-bar.component.css'
})
export class NavBarComponent implements OnInit {

  constructor(private userService: UserService) {
  }

  isUserAuthenticated!: boolean;
  username: any;

  ngOnInit(): void {
    this.userService.isAuthenticated$.subscribe(
      (data) => {
        this.isUserAuthenticated = data
        if (data) {
          this.username = this.userService.username;
        }
      },
      (error) => console.log(error)
    )
  }

  logout() {
    this.userService.logout();
  }


}
