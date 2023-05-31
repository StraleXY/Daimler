import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FavoriteRoutesCardComponent } from './favorite-routes-card.component';

describe('FavoriteRoutesCardComponent', () => {
  let component: FavoriteRoutesCardComponent;
  let fixture: ComponentFixture<FavoriteRoutesCardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ FavoriteRoutesCardComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FavoriteRoutesCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
