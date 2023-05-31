import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PickProfilePicComponent } from './pick-profile-pic.component';

describe('PickProfilePicComponent', () => {
  let component: PickProfilePicComponent;
  let fixture: ComponentFixture<PickProfilePicComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PickProfilePicComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PickProfilePicComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
