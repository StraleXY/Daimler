import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UsersPreviewCardComponent } from './users-preview-card.component';

describe('UsersPreviewCardComponent', () => {
  let component: UsersPreviewCardComponent;
  let fixture: ComponentFixture<UsersPreviewCardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ UsersPreviewCardComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UsersPreviewCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
