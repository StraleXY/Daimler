import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MoreRideSettingsComponent } from './more-ride-settings.component';

describe('MoreRideSettingsComponent', () => {
  let component: MoreRideSettingsComponent;
  let fixture: ComponentFixture<MoreRideSettingsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MoreRideSettingsComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MoreRideSettingsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
