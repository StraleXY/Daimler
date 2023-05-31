import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PassengerAccountComponent } from './passenger-account.component';

describe('PassengerAccountComponent', () => {
  let component: PassengerAccountComponent;
  let fixture: ComponentFixture<PassengerAccountComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PassengerAccountComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PassengerAccountComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
