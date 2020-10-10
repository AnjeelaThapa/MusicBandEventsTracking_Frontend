import {Component, OnInit} from '@angular/core';
import {NavService} from '../../nav-service.service';
import {AuthenticationService} from '../../../feature/authentication/service/authentication.service';
import {MatSnackBar} from '@angular/material';
import {CheckActiveUserService} from '../../../core/check-active-user.service';
import {Router} from '@angular/router';

@Component({
  selector: 'app-top-nav',
  templateUrl: './top-nav.component.html',
  styleUrls: ['./top-nav.component.scss']
})
export class TopNavComponent implements OnInit {
  visitor = true;
  client = false;
  admin = false;

  constructor(public navService: NavService,
              private authService: AuthenticationService,
              private snackBar: MatSnackBar,
              private checkActiveUserService: CheckActiveUserService,
              private router: Router) {
  }

  ngOnInit() {
    this.checkActiveUserService.checkActiveUser();
    this.checkActiveUserService.activeUserObservable.subscribe(userType => {
      switch (userType) {
        case 'none':
          this.visitor = true;
          this.admin = this.client = false;
          break;
        case 'client':
          this.client = true;
          this.admin = this.visitor = false;
          break;
        case 'admin':
          this.admin = true;
          this.visitor = this.client = false;
          break;
      }
    });
  }

  logOutAll() {
    this.authService.getActiveUser().subscribe(activeUserResponse => {
      this.authService.logOutAll(activeUserResponse).subscribe(() => {
        this.visitor = true;
        this.client = this.admin = false;
        localStorage.clear();
        this.checkActiveUserService.changeCurrentUserId(undefined);
        this.checkActiveUserService.changeCurrentUserToken(undefined);
        this.checkActiveUserService.changeCurrentUserType('none');
        this.router.navigate(['/home']).finally(() => {
          this.snackBar.open('Successfully Logged out!', 'Close', {duration: 2000});
        });
      }, error => {
        this.snackBar.open(error.error, 'Close', {duration: 2000, panelClass: ['danger-snack-bar']});
        console.log(error);
      });
    });
  }

  logOut() {

  }
}
