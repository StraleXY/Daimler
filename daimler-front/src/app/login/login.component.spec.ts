import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { BrowserModule } from '@angular/platform-browser';

import { LoginComponent } from './login.component';
import { DatePipe } from '@angular/common';
import { NavMainComponent } from '../nav-main/nav-main.component';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ 
        LoginComponent,
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
        DatePipe
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should make form invalid for empty input', () => {
    component.loginForm.controls['email'].setValue('');
    component.loginForm.controls['password'].setValue('');
    expect(component.loginForm.valid).toBeFalsy();
  });

  it('should make form invalid for invalid email', () => {
    component.loginForm.controls['email'].setValue('abc');
    component.loginForm.controls['password'].setValue('strong_Passw0rd');
    expect(component.loginForm.valid).toBeFalsy();
  });

  it('should make form invalid for short password', () => {
    component.loginForm.controls['email'].setValue('driver@gmail.com');
    component.loginForm.controls['password'].setValue('123');
    expect(component.loginForm.valid).toBeFalsy();
  });

  it('should make form valid', () => {
    component.loginForm.controls['email'].setValue('driver@gmail.com');
    component.loginForm.controls['password'].setValue('strong_Passw0rd');
    expect(component.loginForm.valid).toBeTruthy();
  });
});
