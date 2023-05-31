import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RouteHistoryCardComponent } from './route-history-card.component';

describe('RouteHistoryCardComponent', () => {
  let component: RouteHistoryCardComponent;
  let fixture: ComponentFixture<RouteHistoryCardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ RouteHistoryCardComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RouteHistoryCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
