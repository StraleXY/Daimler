import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DetailedRidePreviewComponent } from './detailed-ride-preview.component';

describe('DetailedRidePreviewComponent', () => {
  let component: DetailedRidePreviewComponent;
  let fixture: ComponentFixture<DetailedRidePreviewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DetailedRidePreviewComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DetailedRidePreviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
