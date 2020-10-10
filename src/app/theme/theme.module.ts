import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {
  MatAutocompleteModule,
  MatButtonModule,
  MatCardModule,
  MatCheckboxModule,
  MatDatepickerModule, MatDialogModule,
  MatFormFieldModule,
  MatIconModule,
  MatInputModule,
  MatListModule,
  MatMenuModule,
  MatNativeDateModule, MatProgressBarModule,
  MatProgressSpinnerModule,
  MatRadioModule,
  MatSelectModule,
  MatSidenavModule,
  MatSliderModule,
  MatSlideToggleModule,
  MatSnackBarModule,
  MatStepperModule,
  MatTabsModule,
  MatToolbarModule
} from '@angular/material';
import {AgmCoreModule} from '@agm/core';
import {environment} from '../../environments/environment';

const BASE_MODULES = [CommonModule, FormsModule, ReactiveFormsModule, AgmCoreModule];

const MAT_MODULES = [
  MatStepperModule,
  MatFormFieldModule,
  MatInputModule,
  MatSliderModule,
  MatRadioModule,
  MatAutocompleteModule,
  MatCheckboxModule,
  MatDatepickerModule,
  MatSelectModule,
  MatSlideToggleModule,
  MatButtonModule,
  MatTabsModule,
  MatCardModule,
  MatProgressSpinnerModule,
  MatIconModule,
  MatToolbarModule,
  MatSidenavModule,
  MatListModule,
  MatNativeDateModule,
  MatMenuModule,
  MatProgressBarModule,
  MatProgressSpinnerModule,
  MatSnackBarModule,
  MatDialogModule
];

@NgModule({
  imports: [
    ...BASE_MODULES,
    ...MAT_MODULES,
    AgmCoreModule.forRoot({
      apiKey: environment.GOOGLE_MAP_API_KEY
    })
  ],
  exports: [
    ...BASE_MODULES,
    ...MAT_MODULES
  ]
})
export class ThemeModule {
}
