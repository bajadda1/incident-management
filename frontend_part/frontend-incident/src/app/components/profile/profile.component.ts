import {Component, OnInit} from '@angular/core';
import {UserService} from '../../services/user-service/user.service';
import {UserResponseDTO} from '../../models/user-response';
import {data} from 'autoprefixer';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css'
})
export class ProfileComponent implements OnInit {

  userInfo!: UserResponseDTO;

  constructor(private userService: UserService) {
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
}
