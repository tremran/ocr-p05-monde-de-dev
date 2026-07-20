import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { of, throwError } from 'rxjs';

import { AuthService } from '../../services/auth.service';
import { LoginComponent } from './login.component';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;

  beforeEach(async () => {
    authServiceSpy = jasmine.createSpyObj<AuthService>('AuthService', ['login']);

    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, RouterTestingModule],
      declarations: [LoginComponent],
      providers: [{ provide: AuthService, useValue: authServiceSpy }],
      schemas: [NO_ERRORS_SCHEMA],
    }).compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call the auth service with the login payload and show a success message', () => {
    authServiceSpy.login.and.returnValue(of({ token: 'fake-token' }));

    component.loginForm.setValue({
      email: 'tester@example.com',
      password: 'StrongPass123',
    });

    component.submit();

    expect(authServiceSpy.login).toHaveBeenCalledOnceWith({
      email: 'tester@example.com',
      password: 'StrongPass123',
    });
    expect(component.loading).toBeFalse();
    expect(component.successMessage).toBe('Login successful.');
    expect(component.errorMessage).toBe('');
    expect(component.loginForm.getRawValue()).toEqual({
      email: '',
      password: '',
    });
  });

  it('should show an error message when the api call fails', () => {
    authServiceSpy.login.and.returnValue(
      throwError(() => new Error('login failed')),
    );

    component.loginForm.setValue({
      email: 'tester@example.com',
      password: 'StrongPass123',
    });

    component.submit();

    expect(authServiceSpy.login).toHaveBeenCalledOnceWith({
      email: 'tester@example.com',
      password: 'StrongPass123',
    });
    expect(component.loading).toBeFalse();
    expect(component.successMessage).toBe('');
    expect(component.errorMessage).toBe(
      'Login failed. Please verify your data and try again.',
    );
  });

  it('should not call the api when the form is invalid', () => {
    component.submit();

    expect(authServiceSpy.login).not.toHaveBeenCalled();
    expect(component.loginForm.controls.email.touched).toBeTrue();
    expect(component.loginForm.controls.password.touched).toBeTrue();
  });
});