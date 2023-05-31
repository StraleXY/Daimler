import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DriverComingComponent } from './driver-coming.component';

describe('DriverComingComponent', () => {
  let component: DriverComingComponent;
  let fixture: ComponentFixture<DriverComingComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DriverComingComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DriverComingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
