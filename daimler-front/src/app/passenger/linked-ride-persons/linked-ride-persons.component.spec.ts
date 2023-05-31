import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LinkedRidePersonsComponent } from './linked-ride-persons.component';

describe('LinkedRidePersonsComponent', () => {
  let component: LinkedRidePersonsComponent;
  let fixture: ComponentFixture<LinkedRidePersonsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ LinkedRidePersonsComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LinkedRidePersonsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
