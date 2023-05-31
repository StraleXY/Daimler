import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DriverNewRideComponent } from './driver-new-ride.component';

describe('DriverNewRideComponent', () => {
  let component: DriverNewRideComponent;
  let fixture: ComponentFixture<DriverNewRideComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DriverNewRideComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DriverNewRideComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
