import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminDriverRegisterComponent } from './admin-driver-register.component';

describe('AdminDriverRegisterComponent', () => {
  let component: AdminDriverRegisterComponent;
  let fixture: ComponentFixture<AdminDriverRegisterComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AdminDriverRegisterComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdminDriverRegisterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
