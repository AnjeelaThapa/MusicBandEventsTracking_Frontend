import {Component, Inject, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {DomSanitizer} from '@angular/platform-browser';
import {Events} from '../../../model/events';
import {EventsService} from '../../services/events.service';
import {MAT_DIALOG_DATA, MatDialogRef, MatSnackBar} from '@angular/material';
import {Router} from '@angular/router';

declare let google: any;

@Component({
  selector: 'app-event-action',
  templateUrl: './events-action.component.html',
  styleUrls: ['./events-action.component.scss']
})
export class EventsActionComponent implements OnInit {
  EventActionForm: FormGroup;
  imagePath;
  defaultProfileImgPath = 'assets/images/placeholder-car-image.jpg';
  zoom = 10;
  latitude = 27.666921;
  longitude = 85.350011;
  markerLatitude = null;
  markerLongitude = null;
  infoWindowOpen: any;
  addressLabel: any;

  forEdit = false;

  constructor(private formBuilder: FormBuilder,
              private sanitizer: DomSanitizer,
              private eventService: EventsService,
              private snackBar: MatSnackBar,
              private router: Router,
              private activeModal: MatDialogRef<EventsActionComponent>,
              @Inject(MAT_DIALOG_DATA) public data: Events) {
  }

  ngOnInit() {
    this.EventActionForm = this.formBuilder.group({
      name: [undefined, Validators.required],
      description: [undefined, Validators.required],
      imgString: ['none', Validators.required],
      schedule: [undefined, Validators.required],
      locationCoordinates: [undefined, Validators.required]
    });

    console.log(this.data);
    if (this.data != null) {
      this.EventActionForm.patchValue(this.data);
      this.imagePath = this.sanitizer.bypassSecurityTrustResourceUrl('data:image/jpg;base64,' + this.data.imgString);
      this.forEdit = true;
    }
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
      this.EventActionForm.get('imgString').patchValue(imageHashCode);
      this.imagePath = this.sanitizer.bypassSecurityTrustResourceUrl('data:image/jpg;base64,' + imageHashCode);
    };
    myReader.readAsDataURL(file);
  }

  findLocation(coordinate) {
    const latLang = coordinate.split(',', 2);
    this.placeMaker(+latLang[0], +latLang[1]);
  }

  placeMaker(latitude, longitude) {
    this.infoWindowOpen = false;
    this.zoom = 10;
    this.latitude = latitude;
    this.longitude = longitude;
    this.markerLatitude = this.latitude;
    this.markerLongitude = this.longitude;
    (this.EventActionForm as FormGroup)
      .get('locationCoordinates')
      .setValue(this.latitude + ',' + this.longitude);
    this.getAddress(this.latitude, this.longitude);
  }

  getAddress(latitude: number, longitude: number) {
    if (navigator.geolocation) {
      const geocoder = new google.maps.Geocoder();
      const latlng = new google.maps.LatLng(latitude, longitude);
      const request = {latLng: latlng};
      geocoder.geocode(request, (results, status) => {
        if (status === google.maps.GeocoderStatus.OK) {
          const result = results[0];
          const rsltAdrComponent = result.formatted_address;
          if (rsltAdrComponent != null) {
            this.addressLabel = rsltAdrComponent;

            this.infoWindowOpen = true;
          } else {
            this.addressLabel = null;
            this.snackBar.open('No address available!', 'Close', {duration: 2000});
          }
        } else {
          this.addressLabel = null;
          this.snackBar.open('Error in GeoCoder', 'Close', {duration: 2000});
        }
      });
    }
  }

  onSubmit() {
    if (this.forEdit) {
      this.data.name = this.EventActionForm.get('name').value;
      this.data.description = this.EventActionForm.get('description').value;
      this.data.imgString = this.EventActionForm.get('imgString').value;
      this.data.schedule = this.EventActionForm.get('schedule').value;
      this.data.locationCoordinates = this.EventActionForm.get('locationCoordinates').value;

      this.eventService.update(this.data._id, this.data).subscribe(res => {
        console.log(res);
        this.activeModal.close();
        this.snackBar.open('Successfully updated event!', 'Close', {duration: 2000, panelClass: ['success-snack-bar']});
      }, error => {
        this.snackBar.open(error.error, 'Close', {duration: 2000, panelClass: ['danger-snack-bar']});
        console.log(error);
      });
    } else {
      const event = this.EventActionForm.value as Events;
      this.eventService.save(event).subscribe(response => {
        console.log(response);
        this.activeModal.close();
        this.snackBar.open('Successfully added event!', 'Close', {duration: 2000, panelClass: ['success-snack-bar']});
      }, error => {
        this.snackBar.open(error.error, 'Close', {duration: 2000, panelClass: ['danger-snack-bar']});
        console.log(error);
      });
    }
  }
}
