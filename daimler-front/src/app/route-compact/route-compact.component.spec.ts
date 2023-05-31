import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RouteCompactComponent } from './route-compact.component';

describe('RouteCompactComponent', () => {
  let component: RouteCompactComponent;
  let fixture: ComponentFixture<RouteCompactComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ RouteCompactComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RouteCompactComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
