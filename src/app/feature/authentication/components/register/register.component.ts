import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {User} from '../../../model/user';
import {AuthenticationService} from '../../service/authentication.service';
import {Router} from '@angular/router';
import {DomSanitizer} from '@angular/platform-browser';
import {AuthResponseModel} from '../../../model/authResponseModel';
import {MatSnackBar} from '@angular/material';
import {CheckActiveUserService} from '../../../../core/check-active-user.service';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent implements OnInit {
  RegisterForm: FormGroup;
  imagePath: any;
  defaultProfileImgPath = 'assets/images/profile-image.png';

  constructor(private formBuilder: FormBuilder,
              private authService: AuthenticationService,
              private router: Router,
              private sanitizer: DomSanitizer,
              private snackBar: MatSnackBar,
              private checkActiveUserService: CheckActiveUserService) {
  }

  ngOnInit() {
    this.buildForm();
  }

  buildForm() {
    this.RegisterForm = this.formBuilder.group({
      username: [undefined, Validators.required],
      fullName: [undefined, Validators.required],
      phoneNumber: [undefined, Validators.required],
      email: [undefined, Validators.required],
      imgString: [undefined, Validators.required],
      passResetToken: ['none', Validators.required],
      type: ['client', Validators.required],
      password: [undefined, Validators.required],
    });
  }

  changeListener($event): void {
    if ($event.target.files[0]) {
      this.readThis($event.target);
    }
  }

  readThis(inputValue: any): void {
    const file: File = inputValue.files[0];
    const myReader: FileReader = new FileReader();
    myReader.onloadend = (e) => {
      let image;
      let imageHashCode;
      image = myReader.result;
      imageHashCode = image.substr(image.indexOf(',') + 1);
      this.RegisterForm.get('imgString').patchValue(imageHashCode);
      this.imagePath = this.sanitizer.bypassSecurityTrustResourceUrl('data:image/jpg;base64,' + imageHashCode);
    };
    myReader.readAsDataURL(file);
  }



  onSubmit() {
    const user = this.RegisterForm.value as User;
    this.authService.save(user).subscribe(response => {
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
