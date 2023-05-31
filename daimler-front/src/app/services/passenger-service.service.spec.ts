import { TestBed } from '@angular/core/testing';

import { PassengerService } from './passenger.service';

describe('PassengerServiceService', () => {
  let service: PassengerService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PassengerService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
