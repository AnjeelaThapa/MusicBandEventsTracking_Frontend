import { Component, OnInit } from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {AuthenticationService} from '../../service/authentication.service';
import {MatSnackBar} from '@angular/material';

@Component({
  selector: 'app-forgot-password',
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.scss']
})
export class ForgotPasswordComponent implements OnInit {
  forgotPasswordForm: FormGroup;
  private resetPasswordForm: FormGroup;

  sentRequest = false;

  constructor(private formBuilder: FormBuilder,
              private authService: AuthenticationService,
              private snackBar: MatSnackBar) { }

  ngOnInit() {
    this.forgotPasswordForm = this.formBuilder.group({
      email: [undefined, Validators.required]
    });
    this.resetPasswordForm = this.formBuilder.group({
      email: [undefined, Validators.required],
      passResetToken: [undefined, Validators.required],
      password: [undefined, Validators.required],
    });
  }

  onSent() {
    this.authService.forgotPasswordSendEmail(this.forgotPasswordForm.get('email').value).subscribe(res => {
      this.sentRequest = true;
      this.snackBar.open('Email Sent, please check your inbox for token', 'Close', {duration: 2000, panelClass: ['success-snack-bar']});
    }, err => {
      this.snackBar.open(err, 'Close', {duration: 2000, panelClass: ['danger-snack-bar']});
      console.log(err);
    });
  }

  onReset() {
    this.resetPasswordForm.get('email').patchValue(this.forgotPasswordForm.get('email').value);
    this.authService.resetPassword(this.resetPasswordForm.value).subscribe(res => {
      this.sentRequest = false;
      this.snackBar.open('Successfully changed password!', 'Close', {duration: 2000, panelClass: ['success-snack-bar']});
    }, err => {
      this.snackBar.open(err, 'Close', {duration: 2000, panelClass: ['danger-snack-bar']});
      console.log(err);
    });
  }
}
