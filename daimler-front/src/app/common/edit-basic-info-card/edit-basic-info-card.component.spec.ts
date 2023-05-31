import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditBasicInfoCardComponent } from './edit-basic-info-card.component';

describe('EditBasicInfoCardComponent', () => {
  let component: EditBasicInfoCardComponent;
  let fixture: ComponentFixture<EditBasicInfoCardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ EditBasicInfoCardComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EditBasicInfoCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
