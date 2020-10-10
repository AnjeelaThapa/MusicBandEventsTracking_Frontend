import {Routes} from '@angular/router';
import {ForgotPasswordComponent} from './components/forgot-password/forgot-password.component';
import {AuthBaseComponent} from './components/auth-base/auth-base.component';

export const AuthRoutes: Routes = [
  {path: '', component: AuthBaseComponent},
  {path: '**', redirectTo: '', pathMatch: 'full'},
];
