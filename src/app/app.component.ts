import {AfterViewInit, Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {CheckActiveUserService} from './core/check-active-user.service';
import {VERSION} from '@angular/material';
import {NavService} from './theme/nav-service.service';
import {NavItem} from './theme/nav-item';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit, AfterViewInit {
  @ViewChild('appDrawer', {static: false}) appDrawer: ElementRef;
  version = VERSION;
  navItems: NavItem[] = [];
  title = 'bandsinTown';

  constructor(private checkActiveUserService: CheckActiveUserService,
              private navService: NavService) {
  }

  ngOnInit(): void {
    this.checkActiveUserService.checkActiveUser();

    this.checkActiveUserService.activeUserObservable.subscribe(userType => {
      switch (userType) {
        case 'none':
          this.navItems = [
            {
              displayName: 'Home',
              iconName: 'home',
              route: '/home'
            },
            {
              displayName: 'Login',
              iconName: 'face',
              route: '/auth'
            },
            {
              displayName: 'Register',
              iconName: 'ac_unit',
              route: '/auth'
            }
          ];
          break;

        case 'client':
          this.navItems = [
            {
              displayName: 'Home',
              iconName: 'home',
              route: '/home'
            },
            {
              displayName: 'Events',
              iconName: 'directions_car',
              children: [
                {
                  displayName: 'Active Events',
                  iconName: 'local_car_wash',
                  route: '/available-events'
                },
                {
                  displayName: 'Bookmarked Events',
                  iconName: 'departure_board',
                  route: '/booked-events'
                }
              ]
            },
            {
              displayName: 'Profile',
              iconName: 'face',
              route: '/profile'
            }
          ];
          break;

        case 'admin':
          this.navItems = [
            {
              displayName: 'Home',
              iconName: 'home',
              route: '/home'
            },
            {
              displayName: 'Events',
              iconName: 'directions_car',
              children: [
                {
                  displayName: 'Events action',
                  iconName: 'local_car_wash',
                  route: '/all-events'
                },
              ]
            },
            {
              displayName: 'Profile',
              iconName: 'face',
              route: '/profile'
            }
          ];
          break;
      }
    });
  }

  ngAfterViewInit() {
    this.navService.appDrawer = this.appDrawer;
  }
}
