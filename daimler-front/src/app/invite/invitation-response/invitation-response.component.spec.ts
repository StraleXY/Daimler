import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InvitationResponseComponent } from './invitation-response.component';

describe('InvitationResponseComponent', () => {
  let component: InvitationResponseComponent;
  let fixture: ComponentFixture<InvitationResponseComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ InvitationResponseComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(InvitationResponseComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
