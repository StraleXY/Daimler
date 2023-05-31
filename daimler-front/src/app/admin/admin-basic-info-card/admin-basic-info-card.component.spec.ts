import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminBasicInfoCardComponent } from './admin-basic-info-card.component';

describe('AdminBasicInfoCardComponent', () => {
  let component: AdminBasicInfoCardComponent;
  let fixture: ComponentFixture<AdminBasicInfoCardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AdminBasicInfoCardComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdminBasicInfoCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
