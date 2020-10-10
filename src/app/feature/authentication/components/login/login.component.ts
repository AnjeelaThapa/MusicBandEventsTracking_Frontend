import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {AuthenticationService} from '../../service/authentication.service';
import {Router} from '@angular/router';
import {MatSnackBar} from '@angular/material';
import {CheckActiveUserService} from '../../../../core/check-active-user.service';
import {User} from '../../../model/user';
import {AuthResponseModel} from '../../../model/authResponseModel';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {
  showSpinner = false;
  loginForm: FormGroup;

  constructor(private formBuilder: FormBuilder,
              private authService: AuthenticationService,
              private router: Router,
              private snackBar: MatSnackBar,
              private checkActiveUserService: CheckActiveUserService) {
  }

  ngOnInit() {
    this.buildForm();
  }

  buildForm() {
    this.loginForm = this.formBuilder.group({
      email: [undefined, Validators.required],
      password: [undefined, Validators.required]
    });
  }

  onSubmit() {
    const userDto = this.loginForm.value as User;
    this.authService.userLogin(userDto).subscribe(response => {
      console.log(response);
      const authResponseModel = response as AuthResponseModel;
      localStorage.setItem('token', authResponseModel.token);
      localStorage.setItem('user_type', authResponseModel.user.type);
      localStorage.setItem('user_id', authResponseModel.user._id);
      this.checkActiveUserService.changeCurrentUserId(authResponseModel.user._id);
      this.checkActiveUserService.changeCurrentUserToken(authResponseModel.token);
      this.checkActiveUserService.changeCurrentUserType(authResponseModel.user.type);
      this.router.navigate(['/home']).finally(() => {
        this.snackBar.open('Successfully registered user!', 'Close', {duration: 2000, panelClass: ['success-snack-bar']});
      });
    }, error => {
      this.snackBar.open(error.error, 'Close', {duration: 2000, panelClass: ['danger-snack-bar']});
      console.log(error);
    });
  }
}
