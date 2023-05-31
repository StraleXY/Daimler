import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RideInProgressPassengerComponent } from './ride-in-progress-passenger.component';

describe('RideInProgressPassengerComponent', () => {
  let component: RideInProgressPassengerComponent;
  let fixture: ComponentFixture<RideInProgressPassengerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ RideInProgressPassengerComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RideInProgressPassengerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
