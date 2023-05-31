import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ScheduledRideComponent } from './scheduled-ride.component';

describe('ScheduledRideComponent', () => {
  let component: ScheduledRideComponent;
  let fixture: ComponentFixture<ScheduledRideComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ScheduledRideComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ScheduledRideComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
