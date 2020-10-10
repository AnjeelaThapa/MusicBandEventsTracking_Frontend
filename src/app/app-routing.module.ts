import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';


const routes: Routes = [
  { path: 'auth', loadChildren: () => import('./feature/authentication/authentication.module').then(m => m.AuthenticationModule) },
  { path: '', loadChildren: () => import('./feature/client/client.module').then(m => m.ClientModule) },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
