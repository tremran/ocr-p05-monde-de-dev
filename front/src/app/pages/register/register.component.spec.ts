import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { of, throwError } from 'rxjs';

import { AuthService } from '../../services/auth.service';
import { RegisterComponent } from './register.component';

describe('RegisterComponent', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;

  beforeEach(async () => {
    authServiceSpy = jasmine.createSpyObj<AuthService>('AuthService', ['register']);

    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, RouterTestingModule],
      declarations: [RegisterComponent],
      providers: [{ provide: AuthService, useValue: authServiceSpy }],
      schemas: [NO_ERRORS_SCHEMA],
    }).compileComponents();

    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call the auth service with the register payload and show a success message', () => {
    authServiceSpy.register.and.returnValue(of({ token: 'fake-token' }));

    component.registerForm.setValue({
      name: 'tester',
      email: 'tester@example.com',
      password: 'StrongPass123',
    });

    component.submit();

    expect(authServiceSpy.register).toHaveBeenCalledOnceWith({
      pseudo: 'tester',
      email: 'tester@example.com',
      password: 'StrongPass123',
    });
    expect(component.loading).toBeFalse();
    expect(component.successMessage).toBe('Registration successful.');
    expect(component.errorMessage).toBe('');
    expect(component.registerForm.getRawValue()).toEqual({
      name: '',
      email: '',
      password: '',
    });
  });

  it('should show an error message when the api call fails', () => {
    authServiceSpy.register.and.returnValue(
      throwError(() => new Error('registration failed')),
    );

    component.registerForm.setValue({
      name: 'tester',
      email: 'tester@example.com',
      password: 'StrongPass123',
    });

    component.submit();

    expect(authServiceSpy.register).toHaveBeenCalledOnceWith({
      pseudo: 'tester',
      email: 'tester@example.com',
      password: 'StrongPass123',
    });
    expect(component.loading).toBeFalse();
    expect(component.successMessage).toBe('');
    expect(component.errorMessage).toBe(
      'Registration failed. Please verify your data and try again.',
    );
  });

  it('should not call the api when the form is invalid', () => {
    component.submit();

    expect(authServiceSpy.register).not.toHaveBeenCalled();
    expect(component.registerForm.controls.name.touched).toBeTrue();
    expect(component.registerForm.controls.email.touched).toBeTrue();
    expect(component.registerForm.controls.password.touched).toBeTrue();
  });
});