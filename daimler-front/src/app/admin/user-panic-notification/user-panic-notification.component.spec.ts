import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserPanicNotificationComponent } from './user-panic-notification.component';

describe('UserPanicNotificationComponent', () => {
  let component: UserPanicNotificationComponent;
  let fixture: ComponentFixture<UserPanicNotificationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ UserPanicNotificationComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UserPanicNotificationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
