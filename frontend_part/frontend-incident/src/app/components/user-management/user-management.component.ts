import {Component, OnInit} from '@angular/core';
import {UserService} from '../../services/user-service/user.service';
import {data} from 'autoprefixer';
import {UserResponseDTO} from '../../models/user-response';

@Component({
  selector: 'app-user-management',
  templateUrl: './user-management.component.html',
  styleUrl: './user-management.component.css'
})
export class UserManagementComponent implements OnInit {

  professionals!: UserResponseDTO[];

  constructor(private userService: UserService) {
  }

  ngOnInit(): void {
    this.getProfessionals();
  }

  getProfessionals(): void {
    this.userService.getProfessionals().subscribe(
      (data) => {
        this.professionals = data;
        console.log("from user management")
        console.log(data)
      },
      (error) => {
        console.log(error);
      }
    )
  }
}
