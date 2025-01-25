import {AfterViewChecked, Component, OnInit} from '@angular/core';

// Initialization for ES Users
import {
  Dropdown,
  Ripple,
  initTWE,
} from "tw-elements";
import {SectorService} from '../../services/sector-service/sector.service';
import {SectorDTO} from '../../models/sector';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {UserService} from '../../services/auth-service/user.service';
import {Router} from '@angular/router';


@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrl: './signup.component.css',
  standalone: false
})
export class SignupComponent implements OnInit {

  formRegister!: FormGroup;
  sectors: SectorDTO[] = [];

  chosenSector!: SectorDTO;

  errorMessage!: string;

  constructor(private userService: UserService,
              private sectorService: SectorService,
              private fb: FormBuilder,
              private router: Router) {
    initTWE({Dropdown, Ripple});
  }


  ngOnInit(): void {

    this.getSector();

    this.formRegister = this.fb.group({
      fullname: this.fb.control("", [
        Validators.required,
        Validators.minLength(2), // Minimum length of 2 characters
        Validators.maxLength(10) // Maximum length of 10 characters
      ]),
      username: this.fb.control("", [
        Validators.required,
        Validators.email // Valid email format
      ]),
      password: this.fb.control("", [
        Validators.required,
        Validators.minLength(6), // Minimum length of 6 characters
        Validators.pattern('^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$') // At least one letter and one number
      ]),
      sectorDTO: this.fb.control(null, [
        Validators.required // Ensure a sector is selected
      ])
    })
  }

  getSector() {
    this.sectorService.getSectors().subscribe(
      (res) => {
        this.sectors = res
        console.log(this.sectors)
      },
      (err) => {
        console.log(err)
        this.errorMessage = 'Failed to load sectors.';
      }
    )
  }

  choseSector(sector: SectorDTO) {
    this.chosenSector = sector; // Update your local variable
    this.formRegister.get('sectorDTO')?.setValue(sector); // Set the form control value

  }

  handleSubmit() {
    if (this.formRegister.invalid) {
      Object.values(this.formRegister.controls).forEach(control => {
        control.markAsTouched(); // Mark all controls as touched to display validation errors
      })
      return;
    }
    this.userService.signup(this.formRegister.value).subscribe({
      next: (resp: any) => {
        console.log(resp)
        this.errorMessage = ''
        this.router.navigateByUrl('/login');
      },
      error: (err: any) => {
        console.log(err)
        this.errorMessage = err.error.message;
      }
    })
  }
}
