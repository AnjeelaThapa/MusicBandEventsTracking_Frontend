import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RedirectToAuthComponent } from './redirect-to-auth.component';

describe('RedirectToAuthComponent', () => {
  let component: RedirectToAuthComponent;
  let fixture: ComponentFixture<RedirectToAuthComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ RedirectToAuthComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RedirectToAuthComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
