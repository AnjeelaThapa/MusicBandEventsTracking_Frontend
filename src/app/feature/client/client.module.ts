import {NgModule} from '@angular/core';
import {HomeComponent} from './component/home/home.component';
import {AboutComponent} from './component/about/about.component';
import {ProfileComponent} from './component/profile/profile.component';
import {RedirectToAuthComponent} from './component/redirect-to-auth/redirect-to-auth.component';
import {ThemeModule} from '../../theme/theme.module';
import {RouterModule} from '@angular/router';
import {ClientFacingRoutes} from './client-routes';
import { ServicesComponent } from './component/services/services.component';
import { EventsComponent } from './component/events/events.component';
import { EventsActionComponent } from './component/events-action/events-action.component';
import { LocationViewerComponent } from './component/location-viewer/location-viewer.component';


@NgModule({
  declarations: [
    HomeComponent,
    AboutComponent,
    ProfileComponent,
    RedirectToAuthComponent,
    ServicesComponent,
    EventsComponent,
    EventsActionComponent,
    LocationViewerComponent
  ],
  imports: [
    ThemeModule,
    RouterModule.forChild(ClientFacingRoutes)
  ],
  entryComponents: [
    EventsActionComponent,
    LocationViewerComponent
  ]
})
export class ClientModule {
}
