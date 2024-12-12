import {Component, OnInit} from '@angular/core';
import {UserRegisterDTO} from '../../models/user-register';
import {UserService} from '../../services/auth-service/user.service';

@Component({
  selector: 'app-user-list',
  templateUrl: './user-list.component.html',
  styleUrl: './user-list.component.css'
})
export class UserListComponent implements OnInit {

  users: UserRegisterDTO[] = [];

  constructor(private userService: UserService) {
  }

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {

  }

}
