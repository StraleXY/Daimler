import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminRideHistoryComponent } from './admin-ride-history.component';

describe('AdminRideHistoryComponent', () => {
  let component: AdminRideHistoryComponent;
  let fixture: ComponentFixture<AdminRideHistoryComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AdminRideHistoryComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdminRideHistoryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
