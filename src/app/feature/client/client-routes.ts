import {Routes} from '@angular/router';
import {HomeComponent} from './component/home/home.component';
import {AboutComponent} from './component/about/about.component';
import {ProfileComponent} from './component/profile/profile.component';
import {ServicesComponent} from './component/services/services.component';
import {EventsComponent} from './component/events/events.component';

export const ClientFacingRoutes: Routes = [
  {path: 'home', component: HomeComponent},
  {path: 'services', component: ServicesComponent},
  {path: 'about', component: AboutComponent},
  {path: 'all-events', component: EventsComponent},
  {path: 'booked-events', component: EventsComponent},
  {path: 'available-events', component: EventsComponent},
  {path: 'profile', component: ProfileComponent},
  {path: '', redirectTo: 'home', pathMatch: 'full'},
  {path: '**', redirectTo: '', pathMatch: 'full'}
];
