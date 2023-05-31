import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DriverUpdateRequestsComponent } from './driver-update-requests.component';

describe('DriverUpdateRequestsComponent', () => {
  let component: DriverUpdateRequestsComponent;
  let fixture: ComponentFixture<DriverUpdateRequestsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DriverUpdateRequestsComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DriverUpdateRequestsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
