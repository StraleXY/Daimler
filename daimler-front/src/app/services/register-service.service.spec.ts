import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { environment } from 'src/environments/environment';
import { UserFull } from '../entities/user';
import { RegisterComponent, User } from '../register/register.component';

import { RegisterService } from './register-service.service';

describe('RegisterService', () => {
  let service: RegisterService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule]
    });
    service = TestBed.inject(RegisterService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should return correct registered user details', () => {
    const registerUserDTO : User = {
      name : "Test name",
      surname : "Test surname",
      profilePicture: "",
      telephoneNumber : "062893673826",
      email : "test@gmail.com",
      address: "Test address",
      password: "goodPassword"
    }

    const registredUserDTO : UserFull = {
      id: 1,
      name: 'Test name',
      surname: 'Test surname',
      profilePicture: '',
      telephoneNumber: '062893673826',
      email: 'test@gmail.com',
      address: 'Test address'
    }

    service.registerUser(registerUserDTO).subscribe(ret => {
      console.log(ret);
      expect(ret.id).toBe(1)
      expect(ret.name).toBe(registerUserDTO.name!)
      expect(ret.surname).toBe(registerUserDTO.surname!)
      expect(ret.telephoneNumber).toBe(registerUserDTO.telephoneNumber!)
      expect(ret.email).toBe(registerUserDTO.email!)
      expect(ret.address).toBe(registerUserDTO.address!)
    })

    const req = httpMock.expectOne(`${environment.apiHost}passenger`);
    expect(req.request.method).toBe("POST");
    req.flush(registredUserDTO)

  });

  it('should return error since email already exists', () => {
    const registerUserDTO : User = {
      name : "Test name",
      surname : "Test surname",
      profilePicture: "",
      telephoneNumber : "062893673826",
      email : "test@gmail.com",
      address: "Test address",
      password: "goodPassword"
    }

    const registredUserDTO : UserFull = {
      id: 1,
      name: 'Test name',
      surname: 'Test surname',
      profilePicture: '',
      telephoneNumber: '062893673826',
      email: 'test@gmail.com',
      address: 'Test address'
    }

    service.registerUser(registerUserDTO).subscribe({
      next: _ => {},
      error: err => {
        expect(err.status).toBe(400)
        expect(err.statusText).toBe("Account with that email already exists")
      }
    })

    const req = httpMock.expectOne(`${environment.apiHost}passenger`);
    expect(req.request.method).toBe("POST");
    req.flush("", { status: 400, statusText: "Account with that email already exists" });

  });

});
