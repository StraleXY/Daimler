import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PassengerInboxComponent } from './passenger-inbox.component';

describe('PassengerInboxComponent', () => {
  let component: PassengerInboxComponent;
  let fixture: ComponentFixture<PassengerInboxComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PassengerInboxComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PassengerInboxComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
