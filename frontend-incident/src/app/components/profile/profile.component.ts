import {Component, OnInit} from '@angular/core';
import { UtilisateurService} from '../../services/user-service/user.service';
import {UserResponseDTO} from '../../models/user-response';
import {data} from 'autoprefixer';
import {UserService} from '../../services/auth-service/user.service';
import {Router} from '@angular/router';

@Component({
    selector: 'app-profile',
    templateUrl: './profile.component.html',
    styleUrl: './profile.component.css',
    standalone: false
})
export class ProfileComponent implements OnInit {

  userInfo!: UserResponseDTO;

  constructor(private userService: UtilisateurService,
              private authService: UserService,
              private router: Router) {
  }

  ngOnInit(): void {
    this.getCurrentUser();
  }

  getCurrentUser() {
    this.userService.getCurrentUser().subscribe(
      (data) => {
        this.userInfo = data
        console.log(this.userInfo)
      },
      error => console.log(error)
    )
  }

  logout() {
    this.authService.logout();
  }
}
