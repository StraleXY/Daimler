import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StatsPreviewComponent } from './stats-preview.component';

describe('StatsPreviewComponent', () => {
  let component: StatsPreviewComponent;
  let fixture: ComponentFixture<StatsPreviewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ StatsPreviewComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(StatsPreviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
