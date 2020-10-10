import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {MatDialog, MatSnackBar} from '@angular/material';
import {CheckActiveUserService} from '../../../../core/check-active-user.service';
import {EventsActionComponent} from '../events-action/events-action.component';
import {EventsService} from '../../services/events.service';
import {Events} from '../../../model/events';
import {DomSanitizer} from '@angular/platform-browser';
import {User} from '../../../model/user';
import {AuthenticationService} from '../../../authentication/service/authentication.service';
import {FormGroup} from '@angular/forms';
import {LocationViewerComponent} from '../location-viewer/location-viewer.component';

declare let google: any;

@Component({
  selector: 'app-events',
  templateUrl: './events.component.html',
  styleUrls: ['./events.component.scss']
})
export class EventsComponent implements OnInit {
  action: string;
  visitor = true;
  client = false;
  admin = false;

  activeUser: User;

  eventList: Events[] = [];

  searchObj = {};

  constructor(private route: ActivatedRoute,
              private dialog: MatDialog,
              private snackBar: MatSnackBar,
              private checkActiveUserService: CheckActiveUserService,
              private eventService: EventsService,
              private sanitizer: DomSanitizer,
              private userService: AuthenticationService,
              private router: Router) {
  }

  ngOnInit() {
    this.userService.getActiveUser().subscribe(res => {
      this.activeUser = res;
      this.route.url.subscribe(params => {
        console.log(params[0].path);
        switch (params[0].path) {
          case 'available-events':
            this.searchObj = {};
            this.eventService.filterEvents(this.searchObj).subscribe(response => {
              this.eventList = response;
            });
            this.action = 'Available';
            break;

          case 'booked-events':
            this.eventList = this.activeUser.bookmarkedEvents;
            this.action = 'Booked';
            break;

          case 'all-events':
            this.searchObj = {};
            this.eventService.filterEvents(this.searchObj).subscribe(response => {
              this.eventList = response;
            });
            this.action = 'Welcome, Admin';
            break;
        }
      });
    }, err => {
      console.log(err);
    });
    this.checkAdmin();
  }

  checkAdmin() {
    this.checkActiveUserService.checkActiveUser();
    this.checkActiveUserService.activeUserObservable.subscribe(userType => {
      switch (userType) {
        case 'none':
          this.visitor = true;
          this.admin = this.client = false;
          break;
        case 'client':
          this.client = true;
          this.admin = this.visitor = false;
          break;
        case 'admin':
          this.admin = true;
          this.visitor = this.client = false;
          break;
      }
    });
  }

  openEventsActionModel(event) {
    const dialogRef = this.dialog.open(EventsActionComponent,
      {
        panelClass: 'action-dialog-container',
        data: event
      });

    dialogRef.afterClosed().subscribe(result => {
      this.ngOnInit();
    });
  }

  openLocation(event) {
    const dialogRef = this.dialog.open(LocationViewerComponent,
      {
        panelClass: 'action-dialog-container',
        data: event
      });

    dialogRef.afterClosed().subscribe(result => {
      this.ngOnInit();
    });
  }

  deleteEvent(events: Events) {
    if (confirm('Are you sure to delete this event?')) {
      this.eventService.delete(events._id).subscribe(res => {
        this.ngOnInit();
        console.log(res);
        this.snackBar.open('Deleted!', 'Close', {duration: 2000});
      }, error => {
        this.snackBar.open(error.error, 'Close', {duration: 2000, panelClass: ['danger-snack-bar']});
        console.log(error);
      });
    }
  }

  bookmarkAction(eventId) {
    const userObj = this.activeUser as User;
    userObj.bookmarkedEvents.push(eventId);
    this.userService.update(userObj._id, userObj).subscribe(res => {
     this.router.navigate(['/booked-events']);
   });
  }

  removeFromBooked(eventId) {
    const userObj = this.activeUser as User;
    userObj.bookmarkedEvents = this.activeUser.bookmarkedEvents.filter(element => element._id !== eventId);
    this.userService.update(userObj._id, userObj).subscribe(res => {
      this.ngOnInit();
    });
  }
}
