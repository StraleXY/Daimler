import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PassengerCountComponent } from './passenger-count.component';

describe('PassengerCountComponent', () => {
  let component: PassengerCountComponent;
  let fixture: ComponentFixture<PassengerCountComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PassengerCountComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PassengerCountComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
