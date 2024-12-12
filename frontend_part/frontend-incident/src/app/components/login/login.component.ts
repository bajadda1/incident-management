import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {Input} from 'postcss';
import {UserService} from '../../services/auth-service/user.service';
import {UserLoginDTO} from '../../models/user-login';
import {data} from 'autoprefixer';
import {Router} from '@angular/router';

// import {error} from '@angular/compiler-cli/src/transformers/util';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent implements OnInit {

  formLogin!: FormGroup;
  errorMessage!: string;


  constructor(private fb: FormBuilder, private userService: UserService, private router: Router) {
  }

  ngOnInit(): void {
    this.formLogin = this.fb.group({
      username: this.fb.control("", [
        Validators.required,
        Validators.email // Valid email format
      ]),
      password: this.fb.control("", [
        Validators.required,
        Validators.minLength(6), // Minimum length of 6 characters
        Validators.pattern('^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$') // At least one letter and one number
      ]),
    })
  }


  submit() {
    if (this.formLogin.invalid) {
      Object.values(this.formLogin.controls).forEach(control => {
        control.markAsTouched(); // Mark all controls as touched to display validation errors
      })
      return;
    }


    const {username, password} = this.formLogin.value;
    console.log(this.formLogin.value)

    this.userService.login({username, password}).subscribe({
      next: (resp: any) => {
        console.log(resp)
        this.errorMessage = ''
        this.router.navigateByUrl('/services/users')
      },
      error: (err: any) => {
        console.error(err.error.message)
        this.errorMessage = err.error.message;
      }
    })
  }


}
