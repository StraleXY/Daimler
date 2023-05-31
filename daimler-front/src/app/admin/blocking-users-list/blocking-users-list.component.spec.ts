import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BlockingUsersListComponent } from './blocking-users-list.component';

describe('BlockingUsersListComponent', () => {
  let component: BlockingUsersListComponent;
  let fixture: ComponentFixture<BlockingUsersListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BlockingUsersListComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BlockingUsersListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
