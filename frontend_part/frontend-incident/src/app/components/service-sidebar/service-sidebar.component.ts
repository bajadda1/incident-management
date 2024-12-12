import {ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {UserService} from '../../services/auth-service/user.service';
import {Router} from '@angular/router';

@Component({
  selector: 'app-service-sidebar',
  templateUrl: './service-sidebar.component.html',
  styleUrl: './service-sidebar.component.css'
})
export class ServiceSidebarComponent implements OnInit {

  constructor(private cdr: ChangeDetectorRef,
              private userService: UserService,
  private router:Router) {
  }


  isSidebarOpen = false;

  toggleSidebar() {
    this.isSidebarOpen = !this.isSidebarOpen;
  }


  isUserAuthenticated: boolean = false;
  role!:any;

  ngOnInit(): void {
    this.userService.isAuthenticated$.subscribe((authStatus) => {
      this.isUserAuthenticated = authStatus;
      // this.role=this.userService.authorities((auth: string)=>auth=="admin")
    });
    console.log(this.role)
  }

  logout() {
    this.userService.logout();
    this.router.navigateByUrl('/login')
  }

}
