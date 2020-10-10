import { Component, OnInit } from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {AuthenticationService} from '../../../authentication/service/authentication.service';
import {Router} from '@angular/router';
import {DomSanitizer} from '@angular/platform-browser';
import {MatSnackBar} from '@angular/material';
import {CheckActiveUserService} from '../../../../core/check-active-user.service';
import {User} from '../../../model/user';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss']
})
export class ProfileComponent implements OnInit {
  activeUser;
  UpdateForm: FormGroup;
  imagePath;
  defaultProfileImgPath = 'assets/images/profile-image.png';


  constructor(private formBuilder: FormBuilder,
              private authService: AuthenticationService,
              private router: Router,
              private sanitizer: DomSanitizer,
              private snackBar: MatSnackBar,
              private checkActiveUserService: CheckActiveUserService) { }

  ngOnInit() {
    this.buildForm();
    this.authService.getActiveUser().subscribe((res: User) => {
      console.log(res);
      this.UpdateForm.patchValue(res);
      this.activeUser = res;
      this.imagePath = this.sanitizer.bypassSecurityTrustResourceUrl('data:image/jpg;base64,' + res.imgString);
    });
  }

  buildForm() {
    this.UpdateForm = this.formBuilder.group({
      username: [undefined, Validators.required],
      fullName: [undefined, Validators.required],
      phoneNumber: [undefined, Validators.required],
      email: [undefined, Validators.required],
      imgString: [undefined, Validators.required],
      passResetToken: ['none', Validators.required],
      type: ['client', Validators.required]
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
      this.UpdateForm.get('imgString').patchValue(imageHashCode);
      this.imagePath = this.sanitizer.bypassSecurityTrustResourceUrl('data:image/jpg;base64,' + imageHashCode);
    };
    myReader.readAsDataURL(file);
  }

  onSubmit() {
    this.activeUser.username = this.UpdateForm.get('username').value;
    this.activeUser.fullName = this.UpdateForm.get('fullName').value;
    this.activeUser.phoneNumber = this.UpdateForm.get('phoneNumber').value;
    this.activeUser.email = this.UpdateForm.get('email').value;
    this.activeUser.imgString = this.UpdateForm.get('imgString').value;
    this.activeUser.passResetToken = this.UpdateForm.get('passResetToken').value;
    this.activeUser.type = this.UpdateForm.get('type').value;

    this.authService.update(this.activeUser._id, this.activeUser).subscribe(response => {
      this.router.navigate(['/home']).finally(() => {
        this.snackBar.open('Successfully updated user!', 'Close', {duration: 2000, panelClass: ['success-snack-bar']});
      });
    }, error => {
      this.snackBar.open(error.error, 'Close', {duration: 2000, panelClass: ['danger-snack-bar']});
      console.log(error);
    });
  }
}
