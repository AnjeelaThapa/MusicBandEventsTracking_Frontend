import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef, MatSnackBar} from '@angular/material';
import {Events} from '../../../model/events';
import {EventsActionComponent} from '../events-action/events-action.component';

declare let google: any;

@Component({
  selector: 'app-location-viewer',
  templateUrl: './location-viewer.component.html',
  styleUrls: ['./location-viewer.component.scss']
})
export class LocationViewerComponent implements OnInit {
  zoom = 10;
  latitude = 27.666921;
  longitude = 85.350011;
  markerLatitude = null;
  markerLongitude = null;
  infoWindowOpen: any;
  addressLabel: any;

  constructor(private snackBar: MatSnackBar,
              private activeModal: MatDialogRef<LocationViewerComponent>,
              @Inject(MAT_DIALOG_DATA) public data: Events) { }

  ngOnInit() {
    this.findLocation(this.data);
  }

  findLocation(coordinate) {
    const latLang = coordinate.split(',', 2);
    console.log(+latLang[0], +latLang[1]);
    this.placeMaker(+latLang[0], +latLang[1]);
  }

  placeMaker(latitude, longitude) {
    this.infoWindowOpen = false;
    this.zoom = 10;
    this.latitude = latitude;
    this.longitude = longitude;
    this.markerLatitude = this.latitude;
    this.markerLongitude = this.longitude;
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

}
