import { NgModule } from '@angular/core';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { ForgotPasswordComponent } from './components/forgot-password/forgot-password.component';
import {ThemeModule} from '../../theme/theme.module';
import { AuthBaseComponent } from './components/auth-base/auth-base.component';
import {RouterModule} from '@angular/router';
import {AuthRoutes} from './auth-routes';



@NgModule({
  declarations: [LoginComponent, RegisterComponent, ForgotPasswordComponent, AuthBaseComponent],
  imports: [
    ThemeModule,
    RouterModule.forChild(AuthRoutes)
  ]
})
export class AuthenticationModule { }
