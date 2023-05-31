import { ComponentFixture, fakeAsync, TestBed, tick } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { BrowserModule, By } from '@angular/platform-browser';

import { RegisterComponent } from './register.component';
import { NavMainComponent } from '../nav-main/nav-main.component';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { DatePipe } from '@angular/common';
import { RegisterService } from '../services/register-service.service';
import { of } from 'rxjs';

describe('RegisterComponent', () => {
  let component: RegisterComponent;
  let registerService: RegisterService;
  let fixture: ComponentFixture<RegisterComponent>;

  beforeEach(async () => {
    const spyRegisterService = jasmine.createSpyObj<RegisterService>(['registerUser']);
    spyRegisterService.registerUser.and.returnValue(of({
      id: 1,
      name: 'Test name',
      surname: 'Test surname',
      profilePicture: '',
      telephoneNumber: '062893673826',
      email: 'test@gmail.com',
      address: 'goodPassword'
    }));
    await TestBed.configureTestingModule({
      declarations: [
        RegisterComponent,
        NavMainComponent,
      ],
      imports: [
        BrowserModule,
        FormsModule,
        ReactiveFormsModule,
        HttpClientTestingModule,
        MatFormFieldModule,
        MatIconModule,
        MatInputModule,
        BrowserAnimationsModule
      ],
      providers: [
        DatePipe,
        {provide: RegisterService, useValue: spyRegisterService}
      ]
    })
    .compileComponents();

    registerService = TestBed.inject(RegisterService);
    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should make form invalid for empty input', () => {
    component.createUserForm.controls['name'].setValue('');
    component.createUserForm.controls['surname'].setValue('');
    component.createUserForm.controls['phone'].setValue('');
    component.createUserForm.controls['email'].setValue('');
    component.createUserForm.controls['address'].setValue('');
    component.createUserForm.controls['password'].setValue('');
    component.createUserForm.controls['confirmPassword'].setValue('');
    expect(component.createUserForm.valid).toBeFalsy();
  });

  it('should make form valid', () => {
    component.createUserForm.controls['name'].setValue('Test');
    component.createUserForm.controls['surname'].setValue('Test');
    component.createUserForm.controls['phone'].setValue('063123456');
    component.createUserForm.controls['email'].setValue('test@gmail.com');
    component.createUserForm.controls['address'].setValue('Testova 12');
    component.createUserForm.controls['password'].setValue('testtest');
    component.createUserForm.controls['confirmPassword'].setValue('testtest');
    expect(component.createUserForm.valid).toBeTruthy();
  });

  it('should make form invalid for any empty field', () => {
    component.createUserForm.controls['name'].setValue('Test');
    component.createUserForm.controls['surname'].setValue('Test');
    component.createUserForm.controls['phone'].setValue('063123456');
    component.createUserForm.controls['email'].setValue('test@gmail.com');
    component.createUserForm.controls['address'].setValue('Testova 12');
    component.createUserForm.controls['password'].setValue('testtest');
    component.createUserForm.controls['confirmPassword'].setValue('testtest');
    expect(component.createUserForm.valid).toBeTruthy();

    component.createUserForm.controls['name'].setValue('');
    expect(component.createUserForm.valid).toBeFalsy();
    component.createUserForm.controls['name'].setValue('Test');

    component.createUserForm.controls['surname'].setValue('');
    expect(component.createUserForm.valid).toBeFalsy();
    component.createUserForm.controls['surname'].setValue('Test');

    component.createUserForm.controls['phone'].setValue('');
    expect(component.createUserForm.valid).toBeFalsy();
    component.createUserForm.controls['phone'].setValue('063123456');

    component.createUserForm.controls['email'].setValue('');
    expect(component.createUserForm.valid).toBeFalsy();
    component.createUserForm.controls['email'].setValue('test@gmail.com');

    component.createUserForm.controls['address'].setValue('');
    expect(component.createUserForm.valid).toBeFalsy();
    component.createUserForm.controls['address'].setValue('Testova 12');

    component.createUserForm.controls['password'].setValue('');
    expect(component.createUserForm.valid).toBeFalsy();
    component.createUserForm.controls['password'].setValue('testtest');

    component.createUserForm.controls['confirmPassword'].setValue('');
    expect(component.createUserForm.valid).toBeFalsy();
    component.createUserForm.controls['confirmPassword'].setValue('testtest');
    expect(component.createUserForm.valid).toBeTruthy();
  });

  it('should make form invalid for bad email regex match', () => {
    component.createUserForm.controls['name'].setValue('Test');
    component.createUserForm.controls['surname'].setValue('Test');
    component.createUserForm.controls['phone'].setValue('063123456');
    component.createUserForm.controls['email'].setValue('test@gmail.com');
    component.createUserForm.controls['address'].setValue('Testova 12');
    component.createUserForm.controls['password'].setValue('testtest');
    component.createUserForm.controls['confirmPassword'].setValue('testtest');
    expect(component.createUserForm.valid).toBeTruthy();

    component.createUserForm.controls['email'].setValue('test');
    expect(component.createUserForm.valid).toBeFalsy();
    component.createUserForm.controls['email'].setValue('test@');
    expect(component.createUserForm.valid).toBeFalsy();
    component.createUserForm.controls['email'].setValue('@123');
    expect(component.createUserForm.valid).toBeFalsy();
    component.createUserForm.controls['email'].setValue('@123@');
    expect(component.createUserForm.valid).toBeFalsy();
    component.createUserForm.controls['email'].setValue('test123@..');
    expect(component.createUserForm.valid).toBeFalsy();
  });

  it('should make form invalid for bad phone regex match', () => {
    component.createUserForm.controls['name'].setValue('Test');
    component.createUserForm.controls['surname'].setValue('Test');
    component.createUserForm.controls['phone'].setValue('063123456');
    component.createUserForm.controls['email'].setValue('test@gmail.com');
    component.createUserForm.controls['address'].setValue('Testova 12');
    component.createUserForm.controls['password'].setValue('testtest');
    component.createUserForm.controls['confirmPassword'].setValue('testtest');
    expect(component.createUserForm.valid).toBeTruthy();

    component.createUserForm.controls['phone'].setValue('0123');
    expect(component.createUserForm.valid).toBeFalsy();
    component.createUserForm.controls['phone'].setValue('test@');
    expect(component.createUserForm.valid).toBeFalsy();
    component.createUserForm.controls['phone'].setValue('aaaaaaa');
    expect(component.createUserForm.valid).toBeFalsy();
    component.createUserForm.controls['phone'].setValue('123abc123');
    expect(component.createUserForm.valid).toBeFalsy();
    component.createUserForm.controls['phone'].setValue('8128481248812481248');
    expect(component.createUserForm.valid).toBeFalsy();
  });

  it('should make form invalid for bad password matching and regex', () => {
    component.createUserForm.controls['name'].setValue('Test');
    component.createUserForm.controls['surname'].setValue('Test');
    component.createUserForm.controls['phone'].setValue('063123456');
    component.createUserForm.controls['email'].setValue('test@gmail.com');
    component.createUserForm.controls['address'].setValue('Testova 12');
    component.createUserForm.controls['password'].setValue('testtest');
    component.createUserForm.controls['confirmPassword'].setValue('testtest');
    expect(component.createUserForm.valid).toBeTruthy();

    component.createUserForm.controls['password'].setValue('0123');
    component.createUserForm.controls['confirmPassword'].setValue('0123');
    expect(component.createUserForm.valid).toBeFalsy();
    component.createUserForm.controls['password'].setValue('goodpassword');
    component.createUserForm.controls['confirmPassword'].setValue('goodpasswordabc');
    expect(component.createUserForm.valid).toBeFalsy();
    component.createUserForm.controls['password'].setValue('goodpasswordabc');
    component.createUserForm.controls['confirmPassword'].setValue('goodpassword');
    expect(component.createUserForm.valid).toBeFalsy();
    component.createUserForm.controls['password'].setValue('goodpassword');
    component.createUserForm.controls['confirmPassword'].setValue('goodpassword');
    expect(component.createUserForm.valid).toBeTruthy();
  });

  it('should hide form on valid input submitted', fakeAsync(() => {
    component.createUserForm.controls['name'].setValue('Test');
    component.createUserForm.controls['surname'].setValue('Test');
    component.createUserForm.controls['phone'].setValue('063123456');
    component.createUserForm.controls['email'].setValue('test@gmail.com');
    component.createUserForm.controls['address'].setValue('Testova 12');
    component.createUserForm.controls['password'].setValue('testtest');
    component.createUserForm.controls['confirmPassword'].setValue('testtest');
    expect(component.createUserForm.valid).toBeTruthy();

    const button = fixture.debugElement.query(By.css('#register-button'));
    (button.nativeElement as HTMLButtonElement).click();
    tick();
    fixture.detectChanges();

    expect(component.showForm).toBeFalse();
  }));
});
