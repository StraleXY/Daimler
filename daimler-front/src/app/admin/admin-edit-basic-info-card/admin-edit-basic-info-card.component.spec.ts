import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminEditBasicInfoCardComponent } from './admin-edit-basic-info-card.component';

describe('AdminEditBasicInfoCardComponent', () => {
  let component: AdminEditBasicInfoCardComponent;
  let fixture: ComponentFixture<AdminEditBasicInfoCardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AdminEditBasicInfoCardComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdminEditBasicInfoCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
