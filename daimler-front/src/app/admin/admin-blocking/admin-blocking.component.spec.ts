import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminBlockingComponent } from './admin-blocking.component';

describe('AdminBlockingComponent', () => {
  let component: AdminBlockingComponent;
  let fixture: ComponentFixture<AdminBlockingComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AdminBlockingComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdminBlockingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
