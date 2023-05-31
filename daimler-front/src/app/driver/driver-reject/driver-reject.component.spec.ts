import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DriverRejectComponent } from './driver-reject.component';

describe('DriverRejectComponent', () => {
  let component: DriverRejectComponent;
  let fixture: ComponentFixture<DriverRejectComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DriverRejectComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DriverRejectComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
