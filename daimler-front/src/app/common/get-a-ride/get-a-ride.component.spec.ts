import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GetARideComponent } from './get-a-ride.component';

describe('GetARideComponent', () => {
  let component: GetARideComponent;
  let fixture: ComponentFixture<GetARideComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ GetARideComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GetARideComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
